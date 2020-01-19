package controllers

import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.libs.json.Json
import play.api.mvc._
import utils.Utils

import scala.concurrent.duration._
import dao._
import play.api.Configuration
import play.api.routing.JavaScriptReverseRouter
import tool.Tool

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2018/7/30
  */
class AppController @Inject()(cc: ControllerComponents, formTool: FormTool, cache: SyncCacheApi,
                              accountDao: AccountDao, userDao: UserDao, tool: Tool, strWeightDao: StrWeightDao,
                              config: Configuration) extends AbstractController(cc) {

  def sendMessage = Action { implicit request =>
    val data = formTool.phoneForm.bindFromRequest().get
    val (b, code, responseCode) = Utils.sendMessage(data.phone)
    if (b) {
      cache.set(data.phone, code, 10 minutes)
      Ok("success!")
    } else {
      Ok(Json.obj("valid" -> "false", "message" -> s"短信发送失败 ${responseCode}！"))
    }
  }

  def validCode = Action { implicit request =>
    val data = formTool.validCodeForm.bindFromRequest().get
    if (cache.get(data.phone).isDefined && cache.get(data.phone).get == data.code) {
      cache.remove(data.phone)
      Ok("success!")
    } else {
      Ok(Json.obj("valid" -> "false", "message" -> "验证码错误！"))
    }
  }

  def toIndex = Action.async { implicit request =>
    val data = formTool.loginUserForm.bindFromRequest().get
    val ip = Utils.getIp
    accountDao.selectById1.zip(userDao.selectByNameOrPhone(data)).flatMap { case (account, optionUser) =>
      if ((data.name == account.account || data.name == account.phone)) {
        val name = account.account
        tool.insertOrUpdateLoginIp(name, ip).map { x =>
          Redirect(routes.AdminController.userManageBefore()).addingToSession("admin" -> account.account)
        }
      } else {
        val dbUser = optionUser.get
        tool.insertOrUpdateLoginIp(dbUser.name, ip).flatMap { x =>
          tool.initWeight(dbUser.id)
        }.map { x =>
          Redirect(routes.UserDataController.manageDataBefore()).addingToSession("user" -> dbUser.name,
            "userId" -> dbUser.id.toString)
        }
      }
    }
  }

  def login = Action.async { implicit request =>
    val data = formTool.loginUserForm.bindFromRequest().get
    val ip = Utils.getIp
    accountDao.selectById1.zip(userDao.selectByNameOrPhone(data)).flatMap { case (account, optionUser) =>
      if ((data.name == account.account || data.name == account.phone) && data.password == account.password) {
        val user = UserData(account.account, account.password, account.phone)
        tool.validLoginIp(user, ip)
      } else if (optionUser.isDefined) {
        val dbUser = optionUser.get
        val user = UserData(dbUser.name, dbUser.password, dbUser.phone)
        tool.validLoginIp(user, ip)
      } else {
        val json = Json.obj("isValid" -> "false", "info" -> "用户名或密码错误!", "isError" -> "true")
        Utils.result2Future(Ok(json))
      }
    }
  }

  def recoverPasswordBefore = Action { implicit request =>
    Ok(views.html.recoverPassword())
  }

  def validName = Action.async { implicit request =>
    val data = formTool.userNameForm.bindFromRequest().get
    userDao.selectByNameOrPhone(data.name).map { x =>
      val valid = if (x.isDefined) "true" else "false"
      val phone = x.map(_.phone)
      Ok(Json.obj("exist" -> valid, "phone" -> phone))
    }
  }

  def newPasswordBefore = Action.async { implicit request =>
    val data = formTool.userNameForm.bindFromRequest().get
    userDao.selectByNameOrPhone(data.name).map { x =>
      Ok(views.html.newPassword(x.get.name))
    }

  }

  def newPassword = Action.async { implicit request =>
    val data = formTool.newPasswordForm.bindFromRequest().get
    userDao.selectByName(data.name).flatMap { x =>
      val dbUser = x.get
      val row = LoginUserData(data.name, data.newPassword)
      userDao.update(row).map { y =>
        Redirect(routes.UserController.loginBefore()).flashing("info" -> s"${data.name}重置密码成功!").removingFromSession("user")
      }
    }
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(

        controllers.routes.javascript.UserDataController.strInferDetailBefore,
        controllers.routes.javascript.UserDataController.getNameInfo,
        controllers.routes.javascript.UserDataController.getNationInfo,
        controllers.routes.javascript.UserDataController.getAdressInfo,
        controllers.routes.javascript.UserDataController.getBasicInfo,
        controllers.routes.javascript.UserDataController.detailInfoBefore,
        controllers.routes.javascript.UserDataController.getSnpNationInfo,
        controllers.routes.javascript.UserDataController.getSnpInfo,
        controllers.routes.javascript.UserDataController.getStrInferData,
        controllers.routes.javascript.UserDataController.detailInfoBefore,
        controllers.routes.javascript.UserDataController.updateDataBefore,
        controllers.routes.javascript.UserDataController.updateData,
        controllers.routes.javascript.UserDataController.getAllBasicInfo,
        controllers.routes.javascript.UserDataController.search,
        controllers.routes.javascript.UserDataController.deleteDataByNumbers,
        controllers.routes.javascript.UserDataController.getDownloadData,

        controllers.routes.javascript.UserSnpDataController.numberCheck,
        controllers.routes.javascript.UserSnpDataController.add2Db,
        controllers.routes.javascript.UserSnpDataController.inferDetailBefore,
        controllers.routes.javascript.UserSnpDataController.getInferData,
        controllers.routes.javascript.UserSnpDataController.getSnpData,
        controllers.routes.javascript.UserSnpDataController.getAllUserSnpDb,
        controllers.routes.javascript.UserSnpDataController.deleteDataByNumber,
        controllers.routes.javascript.UserSnpDataController.refreshSnpStat,
        controllers.routes.javascript.UserSnpDataController.addBatch,
        controllers.routes.javascript.UserSnpDataController.deletes,
        controllers.routes.javascript.UserSnpDataController.batchRefresh,

        controllers.routes.javascript.UserStrDataController.numberCheck,
        controllers.routes.javascript.UserStrDataController.add2Db,
        controllers.routes.javascript.UserStrDataController.refreshStrInfer,
        controllers.routes.javascript.UserStrDataController.getStrData,
        controllers.routes.javascript.UserStrDataController.deleteDataByNumber,
        controllers.routes.javascript.UserStrDataController.getAllUserStrDb,
        controllers.routes.javascript.UserStrDataController.getUserStrData,
        controllers.routes.javascript.UserStrDataController.addBatch,
        controllers.routes.javascript.UserStrDataController.deletes,
        controllers.routes.javascript.UserStrDataController.batchRefresh,

        controllers.routes.javascript.WeightController.getAllWeight,
        controllers.routes.javascript.WeightController.updateWeight,

        controllers.routes.javascript.AdminController.getAllStrDb,
        controllers.routes.javascript.AdminController.updateStrDb,



      )
    ).as("text/javascript")
  }

}
