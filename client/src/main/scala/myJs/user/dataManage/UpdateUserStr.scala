package myJs.user.dataManage

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Tool
import myJs.Tool._
import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._
import org.scalajs.dom.Element
import scalatags.Text.all._
import shared.Shared

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("UpdateUserStr")
object UpdateUserStr {

  val parentStr = "#updateUserStrModal"

  @JSExport("init")
  def init = {
    fillSnpKitName

  }

  def fillSnpKitName = {
    val optionHtml = Shared.panels.map { x =>
      option(value := x, x)
    }.mkString
    $(s"${parentStr} select[name='kind']").html(optionHtml)
  }

  def fillValue(number: String) = {
    fillBasicInfo(number)

  }

  def fillBasicInfo(number: String) = {
    val url = g.jsRoutes.controllers.UserStrDataController.getUserStrData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").`type`("get").
      success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        rs.foreach { case (key, value) =>
          val finalValue = if (value.toString == "") "" else value.toString
          $(s"${parentStr} :input[name='${key}']").`val`(finalValue).trigger("change")
        }
      }
    $.ajax(ajaxSettings)

  }

  @JSExport("update")
  def update = {
    val index = layer.alert(element, layerOptions)
    val url = g.jsRoutes.controllers.UserStrDataController.add2Db().url.toString
    val data = $(s"${parentStr} #form").serialize()
    val ajaxSettings = JQueryAjaxSettings.url(url).data(data).
      `type`("post").success { (data, status, e) =>
      DataManage.refreshTable { () =>
        layer.close(index)
        jQuery(parentStr).modal("hide")
        val options = SwalOptions.`type`("success").title("成功!").text("修改成功")
        Swal.swal(options)
      }
    }
    $.ajax(ajaxSettings)

  }


}
