package controllers

import java.io.File

import dao._
import javax.inject.Inject
import play.api.mvc._
import models.Tables._
import org.joda.time.DateTime
import play.api.libs.json.Json
import shared.Shared
import tool.Tool
import utils.Utils

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import utils.Implicits._

import scala.collection.mutable

/**
  * Created by yz on 2018/7/18
  */
class AdminController @Inject()(cc: ControllerComponents, tool: Tool, basicInfoDao: BasicInfoDao, strDataDao: StrDataDao,
                                snpDataDao: SnpDataDao, strDbDao: StrDbDao,
                                accountDao: AccountDao,
                                formTool: FormTool,
                                userDao: UserDao)
  extends AbstractController(cc) {

  def userManageBefore = Action { implicit request =>
    Ok(views.html.admin.userManage())
  }

  def strDbManageBefore = Action { implicit request =>
    Ok(views.html.admin.strDbManage())
  }

  def getAllStrDb = Action.async { implicit request =>
    strDbDao.selectAll.map { rows =>
      val array = Utils.getArrayByTs(rows, Seq("value"))
      val columnNames = ArrayBuffer("number", "kind", "familyName", "nation", "address")
      val json = Json.obj("columnNames" -> columnNames, "array" -> array)
      Ok(json)
    }

  }

  def updateStrDb = Action.async(parse.multipartFormData) { request =>
    val file = request.body.file("file").get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val tmpFile = new File(tmpDir, "tmp.txt")
    file.ref.moveTo(tmpFile, true)
    val myMessage = fileCheck(tmpFile)
    if (!myMessage.valid) {
      tool.deleteDirectory(tmpDir)
      Future.successful(Ok(Json.obj("error" -> myMessage.message)))
    } else {
      val lines = tmpFile.lines
      val headers = lines.headers
      val rows = lines.drop(1).mapOtherByColumns { columns =>
        val lineMap = headers.zip(columns).toMap
        val siteNames = Shared.strWeightSiteNames
        val map = siteNames.map { siteName =>
          val data = lineMap.getOrElse(siteName, "")
          (siteName, data)
        }.toMap
        StrDbRow(number = lineMap("样本编号"), kind = lineMap("单倍型"), Utils.map2Str(map), familyName = lineMap.getOrElse("姓氏", ""),
          nation = lineMap.getOrElse("民族", ""), address = lineMap.getOrElse("地理", ""))
      }
      strDbDao.updates(rows).map { x =>
        tool.deleteDirectory(tmpDir)
        Ok("success!")
      }
    }

  }

  case class MyMessage(valid: Boolean, message: String)

  def fileCheck(file: File): MyMessage = {
    val lines = file.lines
    val headers = lines.head.split("\t")
    val repeatHeaders = headers.diff(headers.distinct)
    if (repeatHeaders.nonEmpty) {
      return MyMessage(false, s"数据文件表头 ${repeatHeaders.head} 重复!")
    }
    val hasHeaders = ArrayBuffer("样本编号", "单倍型")
    val noExistHeaders = hasHeaders.diff(headers)
    if (noExistHeaders.nonEmpty) {
      return MyMessage(false, s"数据文件表头 ${noExistHeaders.head} 不存在!")
    }
    val repeatColumns = ArrayBuffer("样本编号")
    val repeatMap = repeatColumns.map(x => (x, mutable.Set[String]())).toMap
    val factorMap = Map("单倍型" -> Shared.snpKitNames)
    lines.drop(1).zipWithIndex.foreach { case (line, i) =>
      val columns = line.split("\t").padTo(headers.size, "")
      val lineMap = headers.zip(columns).toMap
      columns.zipWithIndex.foreach { case (tmpColumn, j) =>
        val column = tmpColumn
        val header = headers(j)
        if (repeatColumns.contains(header)) {
          if (repeatMap(header).contains(column)) {
            return MyMessage(false, s"数据文件第${i + 2}行第${j + 1}列重复!")
          } else repeatMap(header) += column
        }
        if (factorMap.keySet.contains(header)) {
          if (!factorMap(header).contains(column)) {
            return MyMessage(false, s"数据文件第${i + 2}行第${j + 1}列只能为(${factorMap(header).mkString("、")})中的一个!")
          }
        }
        if (header == "样本编号") {
          if (!column.startsWith(Shared.companyPrefix)) {
            return MyMessage(false, s"数据文件第${i + 2}行第${j + 1}列，必须以${Shared.companyPrefix}开头!")
          }
        }

      }
    }
    MyMessage(true, "")

  }

  def downloadStrDb = Action.async { implicit request =>
    strDbDao.selectAll.map { case strDbs =>
      val basicHeaders = ArrayBuffer("样本编号", "单倍型", "姓氏", "民族", "地理")
      val strSiteNames = Shared.strWeightSiteNames
      val headers = basicHeaders ++ strSiteNames
      val lines = ArrayBuffer(headers)
      val tmpDir = tool.createTempDirectory("tmpDir")
      strDbs.foreach { strDb =>
        val map = Utils.str2Map(strDb.value)
        val datas = strSiteNames.map { siteName =>
          map.getOrElse(siteName, "")
        }
        lines += ArrayBuffer(strDb.number, strDb.kind, strDb.familyName, strDb.nation, strDb.address) ++ datas
      }
      val file = new File(tmpDir, "str_db.txt")
      lines.map(_.mkString("\t")).toFile(file)
      Ok.sendFile(file, onClose = () => tool.deleteDirectory(tmpDir)).as("application/x-download").
        withHeaders(
          CONTENT_DISPOSITION -> ("attachment; filename=" + file.getName)
        )
    }

  }

  def logout = Action {
    implicit request =>
      Redirect(routes.UserController.loginBefore())
        .flashing("info" -> "退出登录成功!")
        .removingFromSession("admin")
  }

  def changePasswordBefore = Action {
    implicit request =>
      Ok(views.html.admin.changePassword())
  }

  def changePassword = Action.async {
    implicit request =>
      val data = formTool.changePasswordForm.bindFromRequest().get
      accountDao.selectById1.flatMap {
        x =>
          if (data.password == x.password) {
            val row = AccountRow(x.id, x.account, data.newPassword, x.phone)
            accountDao.update(row).map {
              y =>
                Redirect(routes.UserController.loginBefore()).flashing("info" -> "密码修改成功!").removingFromSession("admin")
            }
          } else {
            Future.successful(
              Redirect(routes.AdminController.changePasswordBefore()).flashing("info" -> "密码错误!")
            )
          }
      }
  }

  def downloadData = Action.async {
    implicit request =>
      userDao.selectAll.zip(basicInfoDao.selectAll).zip(strDataDao.selectAll).zip(snpDataDao.selectAll).
        map {
          case (((users, basics), strs), snps) =>
            val basicHeaders = ArrayBuffer("样本编号", "鉴定委托单位", "样本类型", "人员姓名", "身份证号码", "性别", "出生日期", "国籍/地区", "民族",
              "户籍地", "户籍地址", "文化程度", "联系方式1", "联系方式2", "备注")
            val strSiteNames = Shared.strWeightSiteNames
            val snpSiteNames = Shared.allSnpSiteNames
            val strHeaders = ArrayBuffer("STR试剂盒名称") ++ strSiteNames
            val snpHeaders = ArrayBuffer("SNP试剂盒名称") ++ snpSiteNames
            val headers = ArrayBuffer("用户名") ++ basicHeaders ++ strHeaders ++ snpHeaders
            val lines = ArrayBuffer(headers)
            val tmpDir = tool.createTempDirectory("tmpDir")
            val basicsMap = basics.groupBy(_.userId)
            val strsMap = strs.groupBy(_.userId)
            val snpsMap = snps.groupBy(_.userId)
            users.filter {
              user =>
                val userBasics = basicsMap.get(user.id)
                userBasics.isDefined
            }.foreach {
              user =>
                val userBasics = basicsMap(user.id)

                case class PanelData(kitName: String = "", map: Map[String, String] = Map[String, String]())

                val userStrs = strsMap.getOrElse(user.id, Seq[StrDataRow]())
                val userSnps = snpsMap.getOrElse(user.id, Seq[SnpDataRow]())
                val strMap = userStrs.map {
                  str =>
                    val userStrData = tool.str2Db(str)
                    (str.number, PanelData(str.kitName, userStrData.map))
                }.toMap
                val snpMap = userSnps.map {
                  snp =>
                    (snp.number, PanelData(snp.kitName, Utils.str2Map(snp.value)))
                }.toMap
                userBasics.map {
                  basic =>
                    val strData = strMap.getOrElse(basic.number, PanelData())
                    val strDatas = strSiteNames.map {
                      siteName =>
                        strData.map.getOrElse(siteName, "")
                    }
                    val snpData = snpMap.getOrElse(basic.number, PanelData())
                    val snpDatas = snpSiteNames.map {
                      siteName =>
                        snpData.map.getOrElse(siteName, "")
                    }
                    lines += ArrayBuffer(user.name) ++ ArrayBuffer(basic.number, basic.unit, basic.sampleType, basic.name, basic.icNumber, basic.sex, basic.
                      birthdate.map(x => x.toString).getOrElse(""),
                      basic.country, basic.nation, basic.residentialPlace, basic.address, basic.culture, basic.contactWay1,
                      basic.contactWay2, basic.comment) ++ ArrayBuffer(strData.kitName) ++ strDatas ++ ArrayBuffer(snpData.kitName) ++ snpDatas
                }.toBuffer
            }
            val file = new File(tmpDir, "data.txt")
            lines.map(_.mkString("\t")).toFile(file)
            Ok.sendFile(file, onClose = () => tool.deleteDirectory(tmpDir)).as("application/x-download").
              withHeaders(
                CONTENT_DISPOSITION -> ("attachment; filename=" + file.getName)
              )
        }

  }


  def getAllUser = Action.async {
    implicit request =>
      getAllUserAction
  }

  def getAllUserAction = {
    userDao.selectAll.map {
      x =>
        val array = Utils.getArrayByTs(x)
        Ok(Json.toJson(array))
    }
  }

  def deleteUserById = Action.async {
    implicit request =>
      val data = formTool.idForm.bindFromRequest().get
      userDao.deleteById(data.id).flatMap {
        x => getAllUserAction
      }
  }

  def addUser = Action.async {
    implicit request =>
      val data = formTool.userForm.bindFromRequest().get
      val row = UserRow(0, data.name, data.password, data.phone, new DateTime())
      userDao.insert(row).flatMap {
        x => getAllUserAction
      }
  }

  def getUserById = Action.async {
    implicit request =>
      val data = formTool.idForm.bindFromRequest().get
      userDao.selectById(data.id).map {
        x => Ok(Utils.getJsonByT(x))
      }
  }

  def userNameCheck = Action.async {
    implicit request =>
      val data = formTool.userNameForm.bindFromRequest.get
      userDao.selectByName(data.name).zip(accountDao.selectById1).map {
        case (optionUser, admin) =>
          optionUser match {
            case Some(y) => Ok(Json.obj("valid" -> false))
            case None =>
              val valid = if (data.name == admin.account) false else true
              Ok(Json.obj("valid" -> valid))
          }
      }
  }

  def phoneCheck = Action.async {
    implicit request =>
      val data = formTool.phoneForm.bindFromRequest.get
      userDao.selectByPhone(data.phone).zip(accountDao.selectById1).map {
        case (optionUser, admin) =>
          optionUser match {
            case Some(y) => Ok(Json.obj("valid" -> false))
            case None =>
              val valid = if (data.phone == admin.phone) false else true
              Ok(Json.obj("valid" -> valid))
          }
      }
  }

}
