package myJs.user

import com.highcharts.HighchartsAliases._
import com.highcharts.HighchartsUtils._
import com.highcharts._
import com.highcharts.config.{Tooltip, _}
import myJs.Tool
import myJs.Tool.{element, layerOptions}
import myJs.Utils._
import myJs.myPkg._
import myJs.myPkg.Implicits._
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared

import scala.collection.mutable.ArrayBuffer
//import org.scalajs.jquery.jQuery
import com.karasiq.bootstrap.Bootstrap.default._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.{JSON, UndefOr}


/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("SnpDataManage")
object SnpDataManage {

  val ids = ArrayBuffer[String]()


  @JSExport("init")
  def init = {
    refreshTable(()=>
      bindEvt,showLoading = true
    )

  }

  def bindEvt = {
    $("#table").on("check.bs.table", () => getIds)
    $("#table").on("uncheck.bs.table", () => getIds)
    $("#table").on("check-all.bs.table", () => getIds)
    $("#table").on("uncheck-all.bs.table", () => getIds)
    $("#table").on("page-change.bs.table", () => getIds)

  }

  def getIds = {
    ids.clear()
    val arrays = g.$("#table").bootstrapTable("getSelections").asInstanceOf[js.Array[js.Dictionary[String]]]
    ids ++= arrays.map { x =>
      x("number")
    }
    if (ids.isEmpty) {
      $(".idsButton").attr("disabled", true)
    } else {
      $(".idsButton").attr("disabled", false)
    }

  }

  def operateFmt: js.Function = (v: js.Any, row: js.Dictionary[Any]) => {
    val deleteStr = a(
      title := "出库",
      onclick := s"SnpDataManage.delete('${row("number")}')",
      cursor.pointer,
      span(
        em(cls := "fa fa-close")
      )
    ).render
    Array(deleteStr).mkString("&nbsp;")

  }

  val operateColumn = js.Array(
    ColumnOptions.field("操作").title("操作").formatter(operateFmt))

  def refreshTable(f:()=>js.Any=()=>(),showLoading:Boolean=false) = {
    val index = if (showLoading) layer.alert(Tool.loadingElement, Tool.layerOptions) else 0
    val url = g.jsRoutes.controllers.UserSnpDataController.getAllUserSnpDb().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      refreshTableContent(data)
      layer.close(index)
      f()
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("deletes")
  def deletes = {
    val options = SwalOptions.title("").text("确定要将选中的数据从库中移除吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消")
    Swal.swal(options, () => {
      val data = js.Dictionary(
        "numbers" -> ids.toJSArray
      )
      val url = g.jsRoutes.controllers.UserSnpDataController.deletes().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
        `type`("post").data(JSON.stringify(data)).success { (data, status, e) =>
        refreshTable { () =>
          Swal.swal(SwalOptions.title("成功").text("移除成功！").`type`("success"))
          getIds
        }

      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("移除失败！").`type`("error"))
      }
      $.ajax(ajaxSettings)
    })

  }

  @JSExport("delete")
  def delete(number: String) = {
    val options = SwalOptions.title("").text("确定要将此数据从库中移除吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消")
    Swal.swal(options, () => {
      val url = g.jsRoutes.controllers.UserSnpDataController.deleteDataByNumber().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").
        `type`("get").contentType("application/json").success { (data, status, e) =>
        refreshTable{()=>
          Swal.swal(SwalOptions.title("成功").text("移除成功").`type`("success"))
        }

      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("移除失败").`type`("error"))
      }
      $.ajax(ajaxSettings)
    })

  }

  def refreshTableContent(data: js.Any) = {
    val rs = data.asInstanceOf[js.Dictionary[js.Any]]
    val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
    val columns =js.Array(checkColumn)++ columnNames.map { columnName =>
      val title = columnName match {
        case "number" => "样本编号"
        case "kind" => "SNP试剂盒"
        case "unit" => "鉴定委托单位"
        case "name" => "人员姓名"
        case _ => columnName
      }
      val fmt = tbFmt(columnName)
      ColumnOptions.field(columnName).title(title).formatter(fmt).sortable(true)
    }.concat(operateColumn)
    val exportOptions = ExportOptions.csvSeparator("\t")
    val options = TableOptions.data(rs("array")).columns(columns).exportOptions(exportOptions).
      exportHiddenColumns(true)
    $("#table").bootstrapTable("destroy").bootstrapTable(options)
    columnNames.drop(9).foreach { x =>
      $("#table").bootstrapTable("hideColumn", x)
      $(s"input:checkbox[value='${x}']").attr("checked", false)
    }

  }

  def tbFmt(columnName: String): js.Function = (v: js.Any) => columnName match {
    case "number" => {
      val url = g.jsRoutes.controllers.UserDataController.detailInfoBefore().url.toString
      a(
        target := "_blank",
        onclick := s"DataManage.detailInfo('${v.toString}')",
      )(v.toString)
    }
    case _ => v
  }

}
