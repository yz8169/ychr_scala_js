package myJs.user.dataManage

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Tool._
import myJs.Utils._
import myJs.myPkg._
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("AddUserStr")
object AddUserStr {

  val parentStr = "#addStrModal"

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

  @JSExport("add")
  def add = {
      val index = layer.alert(element, layerOptions)
      val url = g.jsRoutes.controllers.UserStrDataController.add2Db().url.toString
      val data = $(s"${parentStr} #form").serialize()
      val ajaxSettings = JQueryAjaxSettings.url(url).data(data).
        `type`("post").success { (data, status, e) =>
        layer.close(index)
        jQuery(parentStr).modal("hide")
        DataManage.refreshTable { () =>
          val options = SwalOptions.`type`("success").title("成功!").text("添加成功")
          Swal.swal(options)
        }

      }
      $.ajax(ajaxSettings)

  }


}
