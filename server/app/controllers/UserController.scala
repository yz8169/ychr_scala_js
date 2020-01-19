package controllers

import dao.{AccountDao, LoginIpDao, UserDao}
import javax.inject.Inject
import models.Tables.LoginIpRow
import org.joda.time.{DateTime, Days}
import play.api.cache._
import play.api.libs.json.Json
import play.api.mvc._
import tool.Tool
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by yz on 2018/6/12
  */
class UserController @Inject()(cc: ControllerComponents, formTool: FormTool, accountDao: AccountDao, userDao: UserDao,
                               cache: SyncCacheApi, loginIpDao: LoginIpDao, tool: Tool) extends AbstractController(cc) {

  def loginBefore = Action { implicit request =>
    Ok(views.html.login())
  }

  def missionManageBefore = Action { implicit request =>
    Ok(views.html.user.missionManage())
  }

  def logout = Action { implicit request =>
    Redirect(routes.UserController.loginBefore()).flashing("info" -> "退出登录成功!").removingFromSession("user")
  }

  def changePasswordBefore = Action { implicit request =>
    Ok(views.html.user.changePassword())
  }

  def changePassword = Action.async { implicit request =>
    val data = formTool.changePasswordForm.bindFromRequest().get
    val name = request.session.get("user").get
    userDao.selectByName(name).flatMap { x =>
      val dbUser = x.get
      if (data.password == dbUser.password) {
        val row = LoginUserData(name, data.newPassword)
        userDao.update(row).map { y =>
          Redirect(routes.UserController.loginBefore()).flashing("info" -> "密码修改成功!").removingFromSession("user")
        }
      } else {
        Future.successful(Redirect(routes.UserController.changePasswordBefore()).flashing("info" -> "密码错误!"))
      }
    }

  }

  def searchBefore = Action { implicit request =>
    Ok(views.html.user.search())
  }





}
