package myJs.user.dataManage

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import myJs.myPkg._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import myJs.myPkg.Implicits._
import shared.Shared

import scala.scalajs.js.JSON


/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("StrInferDetail")
object StrInferDetail {

  @JSExport("init")
  def init = {

  }

  def refreshTable(number: String) = {
    val parentStr = "#strStatModal"
    val url = g.jsRoutes.controllers.UserDataController.getStrInferData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
      val columns = columnNames.map { columnName =>
        val title = columnName match {
          case "index" => "样本编号"
          case "distance" => "距离"
          case "kind" => "单倍型"
          case "name" => "姓氏"
          case "nation" => "民族"
          case "address" => "地理"
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

  def tbFmt(columnName: String): js.Function = (v: js.Any) => columnName match {
    case "index" => {
      val url = g.jsRoutes.controllers.UserDataController.detailInfoBefore().url.toString
      val vStr = v.toString
      if (vStr.startsWith(Shared.companyPrefix)) {
        vStr
      } else {
        a(
          target := "_blank",
          href := s"${url}?number=${vStr}"
        )(vStr).render
      }

    }
    case _ => v
  }

}
