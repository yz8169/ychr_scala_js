package myJs.user.snp

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import myJs.myPkg._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import myJs.myPkg.Implicits._



/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("SnpInferDetail")
object InferDetail {

  val number = $("input[name='number']").`val`()

  @JSExport("init")
  def init = {
//    fillValue
//    refreshTable

  }

  def refreshTable(number:String) = {
    val parentStr="#snpStatModal"
    val url = g.jsRoutes.controllers.UserSnpDataController.getInferData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
      val columns = columnNames.map { columnName =>
        val title = columnName match {
          case "rate" => "相似度（%）"
          case "index" => "样本编号"
          case _ => columnName
        }
        val fmt = tbFmt(columnName)
        ColumnOptions.field(columnName).title(title).sortable(true).formatter(fmt)
      }
      val exportOptions = ExportOptions.csvSeparator("\t").fileName(number)
      val options = TableOptions.data(rs("array")).columns(columns).exportOptions(exportOptions).
        exportHiddenColumns(true)
      $(s"${parentStr} #table").bootstrapTable("destroy").bootstrapTable(options)
      columnNames.drop(9).foreach { x =>
        $(s"${parentStr} #table").bootstrapTable("hideColumn", x)
        $(s"input:checkbox[value='${x}']").attr("checked", false)
      }
    }
    $.ajax(ajaxSettings)

  }

  def tbFmt(columnName: String): js.Function = (v: String) => columnName match {
    case "index" => {
      val url = g.jsRoutes.controllers.UserDataController.detailInfoBefore().url.toString
      a(
        target := "_blank",
        href := s"${url}?number=${v}"
      )(v)
    }
    case _ => v
  }


  def fillValue = {
    fillBasicInfo
    fillSnpData

  }

  def fillBasicInfo = {
    val url = g.jsRoutes.controllers.UserDataController.getBasicInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").`type`("get").
      success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        rs.foreach { case (key, value) =>
          $(s"#${key}").text(value.toString)
        }
      }
    $.ajax(ajaxSettings)

  }

  def fillSnpData = {
    val url = g.jsRoutes.controllers.UserSnpDataController.getSnpData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").`type`("get").
      success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
        val kitName = rs.head.myGet("kitName")
        $("#kitName").text(kitName)
        val html = rs.map { x =>
          val myValue = x.myGet("value")
          div(cls := "form-group",
            div(cls := "myLabel", x.myGet("siteName")),
            input(`type` := "text", id := "rg-from", name := "rg-from", value := myValue, cls := "form-control", readonly)
          )
        }.mkString("&nbsp;")
        $("#snp").html(html)
      }
    $.ajax(ajaxSettings)

  }

  @JSExport("selectAll")
  def selectAll = {
    $(":checkbox").each { (y) =>
      $(y).prop("checked", true)
      $("#table").bootstrapTable("showColumn", $(y).`val`())
    }

  }

  @JSExport("reverseSelect")
  def reverseSelect = {
    $(":checkbox").each { (y: Element, i: Int) =>
      val b = $(y).prop("checked").asInstanceOf[Boolean]
      $(y).prop("checked", !b)
      val value = $(y).`val`()
      if (b) {
        $("#table").bootstrapTable("hideColumn", value)
      } else {
        $("#table").bootstrapTable("showColumn", value)
      }
    }

  }

}
