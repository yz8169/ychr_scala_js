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
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("NPUpdateInfo")
object NPUpdateInfo {

  val number = $("input[name='number']").`val`()
  val parentStr = ""


  @JSExport("init")
  def init = {
    select2Init
    dateInit
    bootStrapValidator

  }

  def select2Init = {
    countryInit
    nationInit
  }

  def dateInit = {
    val options = DatepickerOptions.format(Tool.pattern).language("zh-CN")
    $(".datePicker").datepicker(options)
  }

  def countryInit = {
    val options = Select2Options.placeholder("--请选择--").data(Tool.countries).dropdownParent($("#updateInfoModal")).
      allowClear(true)
    $(".country").select2(options)
  }

  def nationInit = {
    val options = Select2Options.placeholder("--请选择--").data(Tool.nations).dropdownParent($("#updateInfoModal")).
      allowClear(true)
    $("#updateInfoModal #nation").select2(options)
  }


  @JSExport("fillValue")
  def fillValue(number: String) = {
    //    fillBasicInfo(number)
    fillStrData(number)
    fillSnpData(number)

  }

  def fillBasicInfo(number: String) = {
    val url = g.jsRoutes.controllers.UserDataController.getBasicInfo().url.toString
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

  def fillSnpData(number: String) = {
    val url = g.jsRoutes.controllers.UserSnpDataController.getSnpData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").`type`("get").
      success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        val kitName = rs.myGet("kitName")
        val b = (kitName != "")
        val optionHtml = Shared.snpKitNames.map { x =>
          option(value := x, x)
        }.mkString
        val kitNameHtml = if (kitName != "") {
          input(cls := "form-control", name := "snpKitName", value := kitName, readonly).render
        } else {
          select(cls := "form-control", name := "snpKitName", borderRadius := 4, onchange := "NPUpdateInfo.snpChange(this)",
            option(value := "", "---请选择---"),
            raw(optionHtml),
          ).render
        }
        $(s"${parentStr} #kitName").html(kitNameHtml)
        val html = if (b) {
          val myV = rs.myGet("value")
          val row = JSON.parse(myV).asInstanceOf[js.Dictionary[String]]
          val sortRow = Tool.snpSort(kitName, row)
          sortRow.map { case (key, myValue) =>
            div(cls := "form-group",
              div(cls := "myLabel", key),
              input(hidden, name := "snpSiteNames[]", value := key),
              input(`type` := "text", id := "rg-from", name := "snpSiteDatas[]", value := myValue, cls := "form-control")
            )
          }.mkString("&nbsp;")
        } else ""
        $(s"${parentStr} #snp").html(html)
      }
    $.ajax(ajaxSettings)

  }

  @JSExport("fillStrData")
  def fillStrData(number: String) = {
    val url = g.jsRoutes.controllers.UserStrDataController.getStrData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").`type`("get").
      success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        val kitName = rs.myGet("kitName")
        val b = (kitName != "")
        val kitNameHtml = if (kitName != "") {
          input(cls := "form-control", name := "strKitName", value := kitName, readonly).render
        } else {
          select(cls := "form-control", name := "strKitName", borderRadius := 4, onchange := "NPUpdateInfo.change(this)",
            option(value := "", "---请选择---"),
            option(value := "QuickLine", "QuickLine"),
            option(value := "PathFinder Plus", "PathFinder Plus"),
          ).render
        }
        $(s"#strKitName").html(kitNameHtml)
        val html = if (b) {
          val myV = rs.myGet("value")
          val row = JSON.parse(myV).asInstanceOf[js.Dictionary[String]]
          val sortRow = Tool.strSort(kitName, row)
          sortRow.map { case (key, myValue) =>
            div(cls := "form-group",
              div(cls := "myLabel", key),
              input(hidden, name := "strSiteNames[]", value := key),
              input(`type` := "text", id := "rg-from", name := "strSiteDatas[]", value := myValue, cls := "form-control")
            )
          }.mkString("&nbsp;")
        } else ""
        $(s"#str").html(html)
      }
    $.ajax(ajaxSettings)

  }

  @JSExport("change")
  def change(y: Element) = {
    val value = $(y).find(">option:selected").`val`()
    refreshStr(value.toString)

  }

  def refreshStr(key: String) = {
    val html = if (key == "") {
      ""
    } else {
      Shared.strMap(key).map { v =>
        div(cls := "form-group",
          div(cls := "myLabel", v),
          input(hidden, name := "strSiteNames[]", value := v),
          input(`type` := "text", id := "rg-from", name := "strSiteDatas[]", value := "", cls := "form-control")
        )
      }.mkString("&nbsp;")
    }
    $(s"${parentStr} #str").html(html)

  }

  @JSExport("snpChange")
  def snpChange(y: Element) = {
    val value = $(y).find(">option:selected").`val`()
    UpdateInfo.refreshSnp(parentStr, value.toString)

  }

  @JSExport("myRun")
  def myRun = {
    val bv = jQuery(s"${parentStr} #form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $(s"${parentStr} #form").serialize()
      val url = g.jsRoutes.controllers.UserDataController.updateData().url.toString
      val index = layer.alert(element, layerOptions)
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        layer.close(index)
        jQuery("#updateInfoModal").modal("hide")
        DataManage.refreshTable { () =>
          Swal.swal(SwalOptions.title("成功").text("更新成功").`type`("success"))
        }

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
        "icNumber" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "regexp" -> js.Dictionary(
              "regexp" -> js.RegExp("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)"),
              "message" -> "身份证号码不合法！",
            )
          )
        ),
        "siteData" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "regexp" -> js.Dictionary(
              "regexp" -> js.RegExp("/(^\\d+$)/"),
              "message" -> "位点数据不合法！",
            )
          )
        ),
      )
    )
    g.$(s"${parentStr} #form").bootstrapValidator(dict)

  }


}
