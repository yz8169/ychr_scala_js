package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

/**
  * Created by yz on 2018/11/28
  */
class StrDbController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def manageBefore = Action { implicit request =>
    Ok(views.html.admin.strDbManage())
  }

}
