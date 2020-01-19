package myJs.admin

import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._
import org.scalajs.dom.FormData
import org.scalajs.dom.raw.HTMLFormElement
import scalatags.Text.all._
import org.scalajs.dom._
//import org.scalajs.jquery.jQuery
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Tool


/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("StrDbManage")
object StrDbManage {

  @JSExport("init")
  def init = {
    refreshTable(showLayer=true)
    bootStrapValidator

  }

  def refreshTable(f: () => js.Any = () => (),showLayer:Boolean=false) = {
    val index = if (showLayer) layer.alert(Tool.loadingElement, Tool.layerOptions) else 0
    val url = g.jsRoutes.controllers.AdminController.getAllStrDb().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      refreshTableContent(data)
      layer.close(index)
      f()
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("addShow")
  def addShow = {
    jQuery("#addModal").modal("show")

  }

  @JSExport("updateStrDb")
  def updateStrDb = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val confirmOptions = LayerOptions.btn(js.Array("继续", "取消")).title("提示")
      val confirmIndex = layer.confirm("此操作将会清空先前库中所有数据，是否继续？", confirmOptions,
        () => updateStrDbExec, () => ())
    }

  }

  def updateStrDbExec = {
    val formData = new FormData(document.getElementById("form").asInstanceOf[HTMLFormElement])
    val element = div(id := "content",
      span(id := "info", "正在上传数据文件",
        span(id := "progress", "。。。")), " ",
      img(src := "/assets/images/running2.gif", cls := "runningImage", width := 30, height := 20)
    ).render
    val index = layer.alert(element, Tool.layerOptions)
    val url = g.jsRoutes.controllers.AdminController.updateStrDb().url.toString
    val xhr = new XMLHttpRequest
    xhr.open("post", url)
    xhr.upload.onprogress = progressHandlingFunction
    xhr.onreadystatechange = (e) => {
      if (xhr.readyState == XMLHttpRequest.DONE) {
        val data = xhr.response
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        if (rs.get("error").isEmpty) {
          //            val options = LayerOptions.closeBtn(0).skin("layui-layer-molv")
          //            layer.alert("数据库更新完成", options)
          refreshTable { () =>
            layer.close(index)
            jQuery("#addModal").modal("hide")
            val options = SwalOptions.`type`("success").title("成功!").text("数据库覆盖完成!")
            Swal.swal(options)
          }

        } else {
          layer.close(index)
          Swal.swal(SwalOptions.title("错误").text(rs("error")).`type`("error"))
        }
      }
    }
    xhr.send(formData)

  }

  def refreshTableContent(data: js.Any) = {
    val rs = data.asInstanceOf[js.Dictionary[js.Any]]
    val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
    val columns = columnNames.map { columnName =>
      val title = columnName match {
        case "number" => "样本编号"
        case "kind" => "单倍型"
        case "familyName" => "姓氏"
        case "nation" => "民族"
        case "address" => "地理"
        case _ => columnName
      }
      val fmt = tbFmt(columnName)
      ColumnOptions.field(columnName).title(title).formatter(fmt).sortable(true)
    }
    val exportOptions = ExportOptions.csvSeparator("\t").fileName("user_str_db").exportHiddenColumns(true)
    val options = TableOptions.data(rs("array")).columns(columns).exportOptions(exportOptions).
      exportHiddenColumns(true)
    $("#table").bootstrapTable("destroy").bootstrapTable(options)
    columnNames.drop(9).foreach { x =>
      $("#table").bootstrapTable("hideColumn", x)
      $(s"input:checkbox[value='${x}']").attr("checked", false)
    }

  }

  def tbFmt(columnName: String): js.Function = (v: js.Any) => columnName match {
    case _ => v
  }

  def bootStrapValidator = {
    val dict = js.Dictionary(
      "feedbackIcons" -> js.Dictionary(
        "valid" -> "glyphicon glyphicon-ok",
        "invalid" -> "glyphicon glyphicon-remove",
        "validating" -> "glyphicon glyphicon-refresh",
      ),
      "fields" -> js.Dictionary(
        "file" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "请选择一个数据文件!"
            ),
            "file" -> js.Dictionary(
              "message" -> "请选择一个Txt(*.txt)格式的数据文件!",
              "extension" -> "txt",
            ),
          )
        ),
      )
    )
    g.$("#form").bootstrapValidator(dict)

  }


}
