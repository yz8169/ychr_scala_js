package controllers

import dao._
import javax.inject.Inject
import models.Tables.StrWeightRow
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import tool.Tool
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by yz on 2018/11/21
  */
class WeightController @Inject()(cc: ControllerComponents, tool: Tool, strWeightDao: StrWeightDao,
                                 formTool: FormTool) extends AbstractController(cc) {

  def weightManageBefore = Action { implicit request =>
    Ok(views.html.admin.weightManage())
  }

  def getAllWeight = Action.async { implicit request =>
    strWeightDao.selectAll(0).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def updateWeight = Action.async { implicit request =>
    val userId = tool.getUserId
    val data = formTool.updateWeightForm.bindFromRequest().get
    val rows = data.siteNames.zip(data.values).map { case (siteName, value) =>
      StrWeightRow(0, siteName, value)
    }
    strWeightDao.updates(rows).map { x =>
      Redirect(routes.WeightController.getAllWeight())
    }
  }

  def getWeightBySiteName = Action.async { implicit request =>
    val data = formTool.siteNameForm.bindFromRequest().get
    val userId = tool.getUserId
    strWeightDao.select(userId, data.siteName).map { x =>
      Ok(Utils.getJsonByT(x))
    }
  }


}
