package myJs.admin

import org.querki.jquery.JQueryAjaxSettings

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared
import myJs.{Tool, Utils}
import myJs.Tool._
import com.karasiq.bootstrap.Bootstrap.default._
import myJs.myPkg.Implicits._
import myJs.myPkg.{Implicits, Swal, SwalOptions}

import scala.scalajs.js

/**
  * Created by yz on 2019/4/15
  */
@JSExportTopLevel("WeightManage")
object WeightManage {

  @JSExport("init")
  def init = {
    bootStrapValidator
    refreshStr


  }

  def refreshStr={
    val url = g.jsRoutes.controllers.WeightController.getAllWeight().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val array=data.asInstanceOf[js.Array[js.Dictionary[String]]]
      val html=array.map{row=>
        val key=row("siteName")
        val v=Tool.emptyfy(row("value"))
        div(cls := "form-group",
          div(cls := "myLabel", key),
          input(hidden, name := "siteNames[]", value := key),
          input(`type` := "text", name := "values[]", value := v, cls := "form-control")
        )
      }.mkString("&nbsp;")
      $(s"#str").html(html)
      g.$("#form").bootstrapValidator("addField", $(s"input[name='values[]']"))

    }
    $.ajax(ajaxSettings)
  }

  @JSExport("update")
  def update={
    val bv = jQuery(s"#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $(s"#form").serialize()
      val url = g.jsRoutes.controllers.WeightController.updateWeight().url.toString
      val index = layer.alert(element, layerOptions)
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        layer.close(index)
        Swal.swal(SwalOptions.title("成功").text("更新成功！").`type`("success"))

      }
      $.ajax(ajaxSettings)

    }


  }

  @JSExport("bootStrapValidator")
  def bootStrapValidator = {
    val dict = js.Dictionary(
      "feedbackIcons" -> js.Dictionary(
        "valid" -> "glyphicon glyphicon-ok",
        "invalid" -> "glyphicon glyphicon-remove",
        "validating" -> "glyphicon glyphicon-refresh",
      ),
      "fields" -> js.Dictionary(
        "values[]" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "numeric" -> js.Dictionary(
              "message" -> "进化速率必须是实数！",
            )
          )
        ),

      )
    )
    g.$(s"#form").bootstrapValidator(dict)

  }

}
