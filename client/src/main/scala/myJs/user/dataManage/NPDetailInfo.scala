package myJs.user.dataManage

import myJs.Tool
import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("NPDetailInfo")
object NPDetailInfo {

  @JSExport("init")
  def init = {
    //    fillValue
    //    refreshTable

  }


  @JSExport("fillValue")
  def fillValue(number: String) = {
    //    fillBasicInfo(number)
    fillStrData(number)
    fillSnpData(number)

  }

  def fillBasicInfo(number: String) = {
    val parentStr = "#detailInfoModal"
    val url = g.jsRoutes.controllers.UserDataController.getBasicInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").`type`("get").
      success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        rs.foreach { case (key, value) =>
          $(s"${parentStr} #${key}").text(value.toString)
        }
      }
    $.ajax(ajaxSettings)

  }

  def fillSnpData(number: String) = {
    val parentStr = ""
    val url = g.jsRoutes.controllers.UserSnpDataController.getSnpData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").`type`("get").
      success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        val kitName = rs.myGet("kitName")
        $(s"${parentStr} #kitName").text(kitName)
        val b = (kitName != "")
        val html = if (b) {
          val myV = rs.myGet("value")
          val row = JSON.parse(myV).asInstanceOf[js.Dictionary[String]]
          val sortRow = Tool.snpSort(kitName, row)
          sortRow.map { case (key, myValue) =>
            div(cls := "form-group",
              div(cls := "myLabel", key),
              input(`type` := "text", id := "rg-from", name := "rg-from", value := myValue, cls := "form-control", readonly)
            )
          }.mkString("&nbsp;")
        } else ""
        $(s"${parentStr} #snp").html(html)
      }
    $.ajax(ajaxSettings)

  }

  @JSExport("fillStrData")
  def fillStrData(number: String) = {
    val parentStr = ""
    val url = g.jsRoutes.controllers.UserStrDataController.getStrData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").`type`("get").
      success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        val kitName = rs.myGet("kitName")
        $(s"${parentStr} #strKitName").text(kitName)
        val b = (kitName != "")
        val html = if (b) {
          val myV = rs.myGet("value")
          val row = JSON.parse(myV).asInstanceOf[js.Dictionary[String]]
          val sortRow = Tool.strSort(kitName, row)
          sortRow.map { case (key, myValue) =>
            div(cls := "form-group",
              div(cls := "myLabel", key),
              input(`type` := "text", id := "rg-from", name := "strSiteDatas[]", value := myValue, cls := "form-control", readonly)
            )
          }.mkString("&nbsp;")
        } else ""
        $(s"${parentStr} #str").html(html)
      }
    $.ajax(ajaxSettings)

  }

}
