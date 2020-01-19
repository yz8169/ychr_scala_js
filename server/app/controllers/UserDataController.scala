package controllers

import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat

import com.typesafe.config.{Config, ConfigMemorySize}
import dao._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import models.Tables._
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import utils.Utils

import com.typesafe.config.parser.ConfigDocumentFactory

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import models.Tables._
import org.joda.time.DateTime
import play.api.Configuration
import play.api.http.HttpConfiguration
import shared.Shared
import tool.Tool

import scala.concurrent.Future
import utils.Implicits._

/**
  * Created by yz on 2018/8/13
  */
class UserDataController @Inject()(cc: ControllerComponents, formTool: FormTool, basicInfoDao: BasicInfoDao,
                                   strDataDao: StrDataDao, snpDataDao: SnpDataDao, tool: Tool, strDbDao: StrDbDao,
                                   strWeightDao: StrWeightDao, userSnpDbDao: UserSnpDbDao, userStrDbDao: UserStrDbDao,
                                  ) extends AbstractController(cc) {

  def loadDataBefore = Action { implicit request =>
    Ok(views.html.user.loadData())
  }

  def loadData = Action.async { implicit request =>
    val userId = tool.getUserId
    val data = formTool.basicInfoForm.bindFromRequest().get
    val strData = formTool.strForm.bindFromRequest().get
    val snpData = formTool.snpForm.bindFromRequest().get
    val strMap = strData.strSiteNames.zip(strData.strSiteDatas).map { case (siteName, siteDataOp) =>
      (siteName, siteDataOp)
    }.toMap
    val strRow = StrDataRow(userId, data.number, strData.strKitName, Json.toJson(strMap).toString())
    val snpMap = snpData.snpSiteNames.zip(snpData.snpSiteDatas).map { case (siteName, siteDataOp) =>
      val siteData = siteDataOp.getOrElse("0")
      (siteName, siteData)
    }.toMap
    val snpRow = SnpDataRow(userId, data.number, snpData.snpKitName, Json.toJson(snpMap).toString())
    val row = BasicInfoRow(userId, data.number, data.unit, data.sampleType, data.name, data.icNumber, data.sex,
      data.birthDate, data.country, data.nation, data.residentialPlace, data.address, data.culture, data.contactWay1,
      data.contactWay2, data.comment, data.inferSnpKitName, new DateTime())
    tool.strInfer(strRow, row).zip(tool.snpStat(snpRow, data.number)).zip(strDataDao.insert(strRow)).
      zip(snpDataDao.insert(snpRow)).
      map { x =>
        Ok("success!")
      }

  }

  def numberCheck = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest.get
    val userId = tool.getUserId
    basicInfoDao.selectByNumber(userId, data.number).map {
      case Some(y) => Ok(Json.obj("valid" -> false))
      case None => Ok(Json.obj("valid" -> true))
    }
  }

  def manageDataBefore = Action { implicit request =>
    Ok(views.html.user.manageData())
  }

  def getAllBasicInfo = Action.async { implicit request =>
    val userId = tool.getUserId
    basicInfoDao.selectAll(userId).flatMap { x =>
      val numbers = x.map(_.number)
      strDataDao.selectByNumbers(userId, numbers).zip(snpDataDao.selectByNumbers(userId, numbers)).
        zip(userSnpDbDao.selectAll(userId)).zip(userStrDbDao.selectAll(userId)).
        map { case (((strDatas, snpDatas), dbSnpData), userStrDatas) =>
          val dbNumbers = dbSnpData.map(_.number).toSet
          val rs = x.map { basicInfo =>
            val strData = strDatas.find(_.number == basicInfo.number)
            val snpData = snpDatas.find(_.number == basicInfo.number)
            (basicInfo, strData, snpData)
          }
          val array = getArrayByBasicInfos(rs, dbNumbers, userStrDatas)
          Ok(Json.toJson(array))
        }
    }
  }

  def getBasicInfo = Action.async { implicit request =>
    val userId = tool.getUserId
    val data = formTool.numberForm.bindFromRequest().get
    basicInfoDao.selectByNumberSome(userId, data.number).map { x =>
      val json = Utils.getMapByT(x)
      Ok(Json.toJson(json))
    }
  }


  def getArrayByBasicInfos(x: Seq[(BasicInfoRow, Option[StrDataRow], Option[SnpDataRow])], numbers: Set[String],
                           userStrDbs: Seq[UserStrDbRow]) = {
    val userStrDbNumberMap = userStrDbs.map(x => (x.number, x)).toMap
    x.map { case (basicInfo, strData, snpData) =>
      val strKitName = strData.map(_.kitName).getOrElse("")
      val snpKitName = snpData.map(_.kitName).getOrElse("")
      val inferSnpKitName = basicInfo.inferSnpKitName.getOrElse("")
      val inSnpStore = numbers.contains(basicInfo.number)
      val map = Utils.getMapByT(basicInfo)
      val userStrDbOp = userStrDbNumberMap.get(basicInfo.number)
      val userStrDbMap = Utils.getMapByOpT(userStrDbOp)
      Json.obj("number" -> basicInfo.number, "unit" -> basicInfo.unit, "sampleType" -> basicInfo.sampleType,
        "strKitName" -> strKitName, "snpKitName" -> snpKitName, "inferSnpKitName" -> inferSnpKitName,
        "inSnpStore" -> inSnpStore) ++ Json.toJsObject(map) ++ Json.toJsObject(userStrDbMap)
    }
  }

  def deleteDataByNumber = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    basicInfoDao.deleteByNumber(userId, data.number).zip(strDataDao.deleteByNumber(userId, data.number)).
      zip(snpDataDao.deleteByNumber(userId, data.number)).zip(userSnpDbDao.deleteByNumber(userId, data.number)).
      zip(userStrDbDao.deleteByNumber(userId, data.number)).map { x =>
      Future {
        Tool.deleteSnpStatFile(userId, data.number)
        val strInferFile = tool.getStrInferFile(userId, data.number)
        Utils.deleteFile(strInferFile)
      }
      Redirect(routes.UserDataController.getAllBasicInfo())
    }
  }

  def deleteDataByNumbers = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    val userId = tool.getUserId
    basicInfoDao.deleteByNumbers(userId, data.numbers).zip(strDataDao.deleteByNumbers(userId, data.numbers)).
      zip(snpDataDao.deleteByNumbers(userId, data.numbers)).zip(userSnpDbDao.deleteByNumbers(userId, data.numbers)).
      zip(userStrDbDao.deleteByNumbers(userId, data.numbers)).map { x =>
      Future {
        data.numbers.foreach { number =>
          Tool.deleteSnpStatFile(userId, number)
          val strInferFile = tool.getStrInferFile(userId, number)
          Utils.deleteFile(strInferFile)
        }
      }
      Redirect(routes.UserDataController.getAllBasicInfo())
    }
  }

  def getDownloadData = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    val numbers = data.numbers
    val userId = tool.getUserId
    val basicHeaders = ArrayBuffer("样本编号", "鉴定委托单位", "样本类型", "人员姓名", "身份证号码", "性别", "出生日期", "国籍/地区", "民族",
      "户籍地", "户籍地址", "文化程度", "联系方式1", "联系方式2", "备注")
    val strSiteNames = Shared.strWeightSiteNames
    val snpSiteNames = Shared.allSnpSiteNames
    val strHeaders = ArrayBuffer("STR试剂盒名称") ++ strSiteNames
    val snpHeaders = ArrayBuffer("SNP试剂盒名称") ++ snpSiteNames
    val headers = basicHeaders ++ strHeaders ++ snpHeaders
    val lines = ArrayBuffer(headers)
    basicInfoDao.selectAll(userId, numbers).zip(strDataDao.selectAll(userId, numbers)).zip(snpDataDao.selectAll(userId, numbers)).
      map { case ((basics, strs), snps) =>
        case class PanelData(kitName: String = "", map: Map[String, String] = Map[String, String]())
        val strMap = strs.map { str =>
          val userStrData = tool.str2Db(str)
          (str.number, PanelData(str.kitName, userStrData.map))
        }.toMap
        val snpMap = snps.map { snp =>
          (snp.number, PanelData(snp.kitName, Utils.str2Map(snp.value)))
        }.toMap
        lines ++= basics.map { basic =>
          val strData = strMap.getOrElse(basic.number, PanelData())
          val strDatas = strSiteNames.map { siteName =>
            strData.map.getOrElse(siteName, "")
          }
          val snpData = snpMap.getOrElse(basic.number, PanelData())
          val snpDatas = snpSiteNames.map { siteName =>
            snpData.map.getOrElse(siteName, "")
          }
          ArrayBuffer(basic.number, basic.unit, basic.sampleType, basic.name, basic.icNumber, basic.sex, basic.
            birthdate.map(x => x.toString).getOrElse(""),
            basic.country, basic.nation, basic.residentialPlace, basic.address, basic.culture, basic.contactWay1,
            basic.contactWay2, basic.comment) ++ ArrayBuffer(strData.kitName) ++ strDatas ++ ArrayBuffer(snpData.kitName) ++ snpDatas
        }.toBuffer
        val str = lines.map(_.mkString("\t")).mkString("\n")
        Ok(Json.toJson(str))
      }

  }


  def updateDataBefore = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    basicInfoDao.selectByNumber(userId, data.number).map(_.get).map { basicInfo =>
      Ok(views.html.user.updateData(basicInfo))
    }

  }

  def updateData = Action.async { implicit request =>
    val userId = tool.getUserId
    val data = formTool.basicInfoForm.bindFromRequest().get
    val strData = formTool.strForm.bindFromRequest().get
    val snpData = formTool.snpForm.bindFromRequest().get
    val strMap = strData.strSiteNames.zip(strData.strSiteDatas).map { case (siteName, siteDataOp) =>
      (siteName, siteDataOp)
    }.toMap
    val strRow = StrDataRow(userId, data.number, strData.strKitName, Json.toJson(strMap).toString())
    val snpMap = snpData.snpSiteNames.zip(snpData.snpSiteDatas).map { case (siteName, siteDataOp) =>
      val siteData = siteDataOp.getOrElse("0")
      (siteName, siteDataOp)
    }.toMap
    val snpRow = SnpDataRow(userId, data.number, snpData.snpKitName, Json.toJson(snpMap).toString())
    basicInfoDao.selectByNumberSome(userId, data.number).flatMap { row =>
      val newData = BasicInfoRow(userId, data.number, data.unit, data.sampleType, data.name, data.icNumber, data.sex,
        data.birthDate, data.country, data.nation, data.residentialPlace, data.address, data.culture, data.contactWay1,
        data.contactWay2, data.comment, data.inferSnpKitName, row.uploadTime)
      strDataDao.update(strRow).flatMap(x => tool.strInfer(strRow, newData)).zip(tool.snpStat(snpRow, data.number)).
        zip(snpDataDao.update(snpRow)).map { x =>
        Ok("success!")
      }
    }

  }

  def detailInfoBefore = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    basicInfoDao.selectByNumber(userId, data.number).map(_.get).map { basicInfo =>
      Ok(views.html.user.detailInfo(basicInfo))
    }

  }

  def getAllSnpKitNames = Action { implicit request =>
    Ok(Json.toJson(Shared.snpKitNames))
  }

  def getAllPanel = Action { implicit request =>
    Ok(Json.toJson(Shared.panels))
  }

  def strInferDetailBefore = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    basicInfoDao.selectByNumber(userId, data.number).map(_.get).zip(strDataDao.selectByNumber(userId, data.number)).
      zip(snpDataDao.selectByNumber(userId, data.number)).map {
      case ((basicInfo, strData), snpData) =>
        Ok(views.html.user.strInferDetail(basicInfo, strData, snpData))
    }
  }

  def getStrInferData = Action.async { implicit request =>
    val userId = tool.getUserId
    val data = formTool.numberForm.bindFromRequest().get
    val file = new File(Utils.userDir, s"${userId}/strInfer/${data.number}.txt")
    val numbers = file.lines.drop(1).map(_.split("\t")(2))
    strDbDao.selectAll(numbers).zip(basicInfoDao.selectAll(userId, numbers)).map { case (x, basicInfos) =>
      case class OtherInfo(name: String, nation: String, address: String)
      val dbMap = x.map(x => (x.number, OtherInfo(x.familyName, x.nation, x.address))).toMap ++ basicInfos.
        filter(x => StringUtils.isNotBlank(x.name)).map { x =>
        val lsName = Shared.getLsName(x.name)
        val city = Shared.getCity(x.residentialPlace)
        (x.number, OtherInfo(lsName, x.nation, city))
      }
      val (columnNames, array) = Utils.getInfoByFile(file, to = 2)
      val newArray = array.map { map =>
        val key = map("index")
        val db = dbMap.getOrElse(key, OtherInfo("", "", ""))
        map ++ Map("nation" -> db.nation, "name" -> db.name, "address" -> db.address)
      }
      val newColumnNames = columnNames ++ ArrayBuffer("name", "nation", "address")
      val json = Json.obj("columnNames" -> newColumnNames, "array" -> newArray)
      Ok(json)
    }

  }

  def getStrJson = Action { implicit request =>
    Ok(Json.toJson(Shared.strMap))
  }

  def getSnpJson = Action { implicit request =>
    Ok(Json.toJson(Shared.snpMap))
  }

  def loadDataByFileBefore = Action { implicit request =>
    Ok(views.html.user.loadDataByFile())
  }

  def getBasicInfoRows(userId: Int, basicTxtFile: File) = {
    val basicRows = ArrayBuffer[BasicInfoRow]()
    val lines = basicTxtFile.lines
    val headers = lines.head.split("\t")
    if (headers.contains("样本编号")) {
      lines.uniqByColumnName("样本编号").drop(1).filter { line =>
        val columns = line.split("\t")
        val index = headers.indexOf("样本编号")
        val number = columns(index)
        StringUtils.isNotEmpty(number) && !Utils.containsChinese(number)
      }.foreach { line =>
        val columns = line.split("\t")
        val map = columns.zip(headers).map { case (value, header) =>
          (header, value)
        }.toMap
        val birthDateStr = map.getOrElse("出生日期", "")
        val birthDate = if (birthDateStr.isBlank) {
          None
        } else {
          val format = new SimpleDateFormat(Shared.pattern)
          val d = format.parse(birthDateStr)
          Some(new Date(d.getTime))
        }

        basicRows += BasicInfoRow(userId = userId, number = map("样本编号"), unit = map.getOrElse("鉴定委托单位", ""),
          sampleType = map.getOrElse("样本类型", "其他"),
          name = map.getOrElse("人员姓名", ""),
          icNumber = map.getOrElse("身份证号码", ""),
          sex = map.getOrElse("性别", ""),
          birthdate = birthDate,
          country = map.getOrElse("国籍/地区", ""),
          nation = map.getOrElse("民族", ""),
          residentialPlace = map.getOrElse("户籍地", ""),
          address = map.getOrElse("户籍地址", ""),
          culture = map.getOrElse("文化程度", ""),
          contactWay1 = map.getOrElse("联系方式1", ""),
          contactWay2 = map.getOrElse("联系方式2", ""),
          comment = map.getOrElse("备注", ""),
          uploadTime = new DateTime()
        )
      }
    }
    basicRows
  }

  def getStrRows(userId: Int, strFile: File) = {
    val lines = strFile.lines
    case class StrData(number: String, siteName: String, value: String)
    val strs = lines.drop(1).map { line =>
      val columns = line.split("\t")
      StrData(columns(0), columns(1), columns.drop(2).mkString(","))
    }
    strs.groupBy(_.number).map { case (number, dbStrs) =>
      val siteNames = dbStrs.map(_.siteName).toSet
      val kitName = Shared.strSiteNameDatas.find { strSiteNameData =>
        val tmpSiteNames = siteNames.map { siteName =>
          strSiteNameData.otherNameMap.getOrElse(siteName, siteName)
        }
        tmpSiteNames == strSiteNameData.siteNames
      }.map(_.kitName).getOrElse("")
      val map = dbStrs.map(x => (x.siteName, x.value)).toMap
      StrDataRow(userId, number, kitName, Json.toJson(map).toString())
    }.filter(_.kitName != "")

  }

  def getSnpRows(userId: Int, snpFile: File) = {
    val lines = snpFile.lines
    case class UserSnpData(number: String, siteName: String, value: String)
    val strs = lines.drop(1).map { line =>
      val columns = line.split("\t")
      UserSnpData(columns(0), columns(1), columns.drop(2).mkString(","))
    }
    strs.groupBy(_.number).map { case (number, dbStrs) =>
      val siteNames = dbStrs.map(_.siteName).toSet
      val kitName = Shared.snpSiteNameDatas.find { strSiteNameData =>
        val tmpSiteNames = siteNames.map { siteName =>
          strSiteNameData.otherNameMap.getOrElse(siteName, siteName)
        }
        tmpSiteNames == strSiteNameData.siteNames
      }.map(_.kitName).getOrElse("")
      val map = dbStrs.map(x => (x.siteName, x.value)).toMap
      SnpDataRow(userId, number, kitName, Json.toJson(map).toString())
    }.filter(_.kitName != "")

  }

  def fileCheck(file: File) = {
    val lines = file.lines
    val b = lines.drop(1).forall(_.split("\t").size >= 2)
    var message = ""
    if (!b) {
      message = "数据文件格式不正确！"
    }
    (b, message)
  }

  def loadDataByFile = Action(parse.multipartFormData).async { implicit request =>
    val tmpDir = tool.createTempDirectory("tmpDir")
    val userId = tool.getUserId
    val basicRows = ArrayBuffer[BasicInfoRow]()
    var valid = true
    var message = ""
    request.body.files.filter(_.key == "basicInfoFile").foreach { tmpFile =>
      if (tmpFile.ref.path.toFile.length() > 0) {
        val basicExcelFile = new File(tmpDir, "basic.xlsx")
        tmpFile.ref.moveTo(basicExcelFile, replace = true)
        val basicTxtFile = new File(tmpDir, "basic.txt")
        Utils.xlsx2Txt(basicExcelFile, basicTxtFile)
        basicRows ++= getBasicInfoRows(userId, basicTxtFile)
      }
    }
    val strRows = ArrayBuffer[StrDataRow]()
    request.body.files.filter(_.key == "strFile").foreach { tmpFile =>
      if (tmpFile.ref.path.toFile.length() > 0) {
        val fileName = tmpFile.filename
        val suffix = Utils.getSuffix(fileName)
        val strFile = new File(tmpDir, "str.txt")
        if (suffix.toLowerCase == "dat") {
          val datFile = new File(tmpDir, "str.dat")
          tmpFile.ref.moveTo(datFile, replace = true)
          Tool.dat2txt(datFile, strFile)
        } else {
          tmpFile.ref.moveTo(strFile, replace = true)
        }
        val (b, error) = fileCheck(strFile)
        if (b) {
          strRows ++= getStrRows(userId, strFile)
        } else {
          valid = false
          message = error
        }

      }
    }
    val snpRows = ArrayBuffer[SnpDataRow]()
    request.body.files.filter(_.key == "snpFile").foreach { tmpFile =>
      if (tmpFile.ref.path.toFile.length() > 0) {
        val fileName = tmpFile.filename
        val suffix = Utils.getSuffix(fileName)
        val snpFile = new File(tmpDir, "snp.txt")
        if (suffix.toLowerCase == "dat") {
          val datFile = new File(tmpDir, "snp.dat")
          tmpFile.ref.moveTo(datFile, replace = true)
          Tool.dat2txt(datFile, snpFile)
        } else {
          tmpFile.ref.moveTo(snpFile, replace = true)
        }
        val (b, error) = fileCheck(snpFile)
        if (b) {
          snpRows ++= getSnpRows(userId, snpFile)
        } else {
          valid = false
          message = error
        }
      }
    }
    val snpNumbers = snpRows.map(_.number).distinct
    val snpRowMap = snpRows.map(x => (x.number, x)).toMap
    userStrDbDao.selectAll(userId, snpNumbers).flatMap { userStrs =>
      val updateUserStrs = userStrs.map { userStr =>
        val snpRow = snpRowMap(userStr.number)
        val kind = snpRow.kitName
        userStr.copy(kind = kind)
      }
      userStrDbDao.updates(updateUserStrs)
    }.flatMap { x =>
      basicInfoDao.selectAll(userId).flatMap { dbBasics =>
        val dbNumbers = dbBasics.map(_.number)
        val numbers = snpNumbers ++ strRows.map(_.number).distinct -- basicRows.map(_.number) -- dbNumbers
        basicRows ++= numbers.map { number =>
          BasicInfoRow(userId = userId, number = number, unit = "", sampleType = "其他", icNumber = "", name = "",
            nation = "", residentialPlace = "", sex = "",
            country = "", uploadTime = new DateTime(), address = "", culture = "", contactWay1 = "", contactWay2 = "",
            comment = "")
        }
        tool.deleteDirectory(tmpDir)
        val strRowMap = strRows.map(x => (x.number, x)).toMap
        if (valid) {
          basicInfoDao.insertOrUpdates(basicRows).zip(strDataDao.insertOrUpdates(strRows)).
            zip(snpDataDao.insertOrUpdates(snpRows)).map { x =>
            val threadNum = 10
            basicRows.grouped(threadNum).foreach { basics =>
              val f = basics.map { basicInfo =>
                (strRowMap.get(basicInfo.number).map { strRow =>
                  tool.strInfer(strRow, basicInfo)
                }.getOrElse(Future {})).zip {
                  snpRowMap.get(basicInfo.number).map { snpRow =>
                    tool.snpStat(snpRow, basicInfo.number)
                  }.getOrElse(Future {})
                }
              }.reduceLeft((x, y) => x flatMap (_ => y))
              Utils.execFuture(f)
            }
          }.map { x =>
            val json = Json.obj("valid" -> true)
            Ok(json)
          }
        } else {
          Utils.result2Future(Ok(Json.obj("valid" -> valid, "message" -> message)))
        }
      }
    }

  }

  def downloadData = Action.async { implicit request =>
    val userId = tool.getUserId
    val basicHeaders = ArrayBuffer("样本编号", "鉴定委托单位", "样本类型", "人员姓名", "身份证号码", "性别", "出生日期", "国籍/地区", "民族",
      "户籍地", "户籍地址", "文化程度", "联系方式1", "联系方式2", "备注")
    val strSiteNames = Shared.strWeightSiteNames
    val snpSiteNames = Shared.allSnpSiteNames
    val strHeaders = ArrayBuffer("STR试剂盒名称") ++ strSiteNames
    val snpHeaders = ArrayBuffer("SNP试剂盒名称") ++ snpSiteNames
    val headers = basicHeaders ++ strHeaders ++ snpHeaders
    val lines = ArrayBuffer(headers)
    val tmpDir = tool.createTempDirectory("tmpDir")
    basicInfoDao.selectAll(userId).zip(strDataDao.selectAll(userId)).zip(snpDataDao.selectAll(userId)).
      map { case ((basics, strs), snps) =>
        case class PanelData(kitName: String = "", map: Map[String, String] = Map[String, String]())
        val strMap = strs.map { str =>
          val userStrData = tool.str2Db(str)
          (str.number, PanelData(str.kitName, userStrData.map))
        }.toMap
        val snpMap = snps.map { snp =>
          (snp.number, PanelData(snp.kitName, Utils.str2Map(snp.value)))
        }.toMap
        lines ++= basics.map { basic =>
          val strData = strMap.getOrElse(basic.number, PanelData())
          val strDatas = strSiteNames.map { siteName =>
            strData.map.getOrElse(siteName, "")
          }
          val snpData = snpMap.getOrElse(basic.number, PanelData())
          val snpDatas = snpSiteNames.map { siteName =>
            snpData.map.getOrElse(siteName, "")
          }
          ArrayBuffer(basic.number, basic.unit, basic.sampleType, basic.name, basic.icNumber, basic.sex, basic.
            birthdate.map(x => x.toString).getOrElse(""),
            basic.country, basic.nation, basic.residentialPlace, basic.address, basic.culture, basic.contactWay1,
            basic.contactWay2, basic.comment) ++ ArrayBuffer(strData.kitName) ++ strDatas ++ ArrayBuffer(snpData.kitName) ++ snpDatas
        }.toBuffer
        val file = new File(tmpDir, "data.txt")
        lines.map(_.mkString("\t")).toFile(file)
        Ok.sendFile(file, onClose = () => tool.deleteDirectory(tmpDir)).as("application/x-download").
          withHeaders(
            CONTENT_DISPOSITION -> ("attachment; filename=" + file.getName)
          )
      }

  }

  def download = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.examplePath, data.fileName)
    Ok.sendFile(file).as("application/x-download").
      withHeaders(
        CACHE_CONTROL -> "max-age=3600",
        CONTENT_DISPOSITION -> ("attachment; filename=" + file.getName)
      )
  }

  def getNameInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    val detailFile = new File(Utils.userDir, s"${userId}/strInfer/${data.number}.txt")
    val numbers = detailFile.lines.drop(1).map(_.split("\t")(2))
    case class Info(percent: BigDecimal, num: Int)
    strDbDao.selectAll(numbers).zip(basicInfoDao.selectAll(userId, numbers)).map { case (x, basicInfos) =>
      val dbNames = x.map(_.familyName).filter(x => StringUtils.isNotBlank(x))
      val otherNames = basicInfos.map(x => x.name).filter(x => StringUtils.isNotBlank(x)).map { x =>
        Shared.getLsName(x)
      }
      val names = dbNames ++ otherNames
      val array = names.groupBy(x => x).mapValues { values =>
        val value = BigDecimal(values.size) / names.size
        val percent = Utils.scale100(value, 1)
        Info(percent, values.size)
      }.toList.sortWith((x, y) => x._2.percent > y._2.percent).map { case (key, info) =>
        Json.obj("name" -> key, "percent" -> s"${info.percent}%", "num" -> info.num)
      }
      Ok(Json.obj("array" -> array, "recordNum" -> names.size))
    }

  }

  def search = Action.async { implicit request =>
    val data = formTool.searchForm.bindFromRequest().get
    val userId = tool.getUserId
    basicInfoDao.selectAll(userId, data).flatMap { x =>
      val numbers = x.map(_.number)
      strDataDao.selectByNumbers(userId, numbers).zip(snpDataDao.selectByNumbers(userId, numbers)).
        zip(userSnpDbDao.selectAll(userId)).zip(userStrDbDao.selectAll(userId)).
        map { case (((strDatas, snpDatas), dbSnpData), userStrDatas) =>
          val dbNumbers = dbSnpData.map(_.number).toSet
          val rs = x.map { basicInfo =>
            val strData = strDatas.find(_.number == basicInfo.number)
            val snpData = snpDatas.find(_.number == basicInfo.number)
            (basicInfo, strData, snpData)
          }
          val array = getArrayByBasicInfos(rs, dbNumbers, userStrDatas)
          Ok(Json.toJson(array))
        }
    }
  }

  def getAdressInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    val detailFile = new File(Utils.userDir, s"${userId}/strInfer/${data.number}.txt")
    val numbers = detailFile.lines.drop(1).map(_.split("\t")(2))
    case class Info(percent: BigDecimal, num: Int)
    strDbDao.selectAll(numbers).zip(basicInfoDao.selectAll(userId, numbers)).map { case (x, basicInfos) =>
      val names = (x.map(_.address) ++ basicInfos.map(x => x.residentialPlace).map { x =>
        Shared.getCity(x)
      }).filter(x => StringUtils.isNotBlank(x))
      val array = names.groupBy(x => x).mapValues { values =>
        val value = BigDecimal(values.size) / names.size
        val percent = Utils.scale100(value, 1)
        Info(percent, values.size)
      }.toList.sortWith((x, y) => x._2.percent > y._2.percent).map { case (key, info) =>
        Json.obj("address" -> key, "percent" -> s"${info.percent}%", "num" -> info.num)
      }
      Ok(Json.obj("array" -> array, "recordNum" -> names.size))
    }

  }

  def getNationInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    val detailFile = new File(Utils.userDir, s"${userId}/strInfer/${data.number}.txt")
    val numbers = detailFile.lines.drop(1).map(_.split("\t")(2))
    case class Info(percent: BigDecimal, num: Int)
    strDbDao.selectAll(numbers).zip(basicInfoDao.selectAll(userId, numbers)).map { case (x, basicInfos) =>
      val names = (x.map(_.nation) ++ basicInfos.map(x => x.nation)).filter(x => StringUtils.isNotBlank(x))
      val array = names.groupBy(x => x).mapValues { values =>
        val value = BigDecimal(values.size) / names.size
        val percent = Utils.scale100(value, 1)
        Info(percent, values.size)
      }.toList.sortWith((x, y) => x._2.percent > y._2.percent).map { case (key, info) =>
        Json.obj("nation" -> key, "percent" -> s"${info.percent}%", "num" -> info.num)
      }
      Ok(Json.obj("array" -> array, "recordNum" -> names.size))
    }

  }

  def getSnpNationInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    val detailFile = Tool.getSnpStatFile(userId, data.number)
    val numbers = detailFile.lines.drop(1).map(_.split("\t")(1))
    case class Info(percent: BigDecimal, num: Int)
    basicInfoDao.selectAll(userId, numbers).map { x =>
      val names = x.map(_.nation).filter(x => StringUtils.isNotBlank(x))
      val array = names.groupBy(x => x).mapValues { values =>
        val value = BigDecimal(values.size) / names.size
        val percent = Utils.scale100(value, 1)
        Info(percent, values.size)
      }.toList.sortWith((x, y) => x._2.percent > y._2.percent).map { case (key, info) =>
        Json.obj("nation" -> key, "percent" -> s"${info.percent}%", "num" -> info.num)
      }
      Ok(Json.obj("array" -> array, "recordNum" -> names.size))
    }

  }

  def getSnpAdressInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    val detailFile = Tool.getSnpStatFile(userId, data.number)
    val numbers = detailFile.lines.drop(1).map(_.split("\t")(1))
    case class Info(percent: BigDecimal, num: Int)
    basicInfoDao.selectAll(userId, numbers).map { x =>
      val names = x.map(_.address).filter(_.notBlank)
      val array = names.groupBy(x => x).mapValues { values =>
        val value = BigDecimal(values.size) / names.size
        val percent = Utils.scale100(value, 1)
        Info(percent, values.size)
      }.toList.sortWith((x, y) => x._2.percent > y._2.percent).map { case (key, info) =>
        Json.obj("address" -> key, "percent" -> s"${info.percent}%", "num" -> info.num)
      }
      Ok(Json.obj("array" -> array, "recordNum" -> names.size))
    }

  }

  def getSnpInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    val detailFile = Tool.getSnpStatFile(userId, data.number)
    val numbers = if (detailFile.exists()) {
      detailFile.lines.drop(1).map(_.split("\t")(1))
    } else mutable.Buffer[String]()
    basicInfoDao.selectAll(userId, numbers).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }

  }


}
