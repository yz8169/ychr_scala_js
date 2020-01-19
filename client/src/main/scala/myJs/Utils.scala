package myJs

import java.sql.Date
import java.text.SimpleDateFormat

import com.highcharts.CleanJsObject
import com.highcharts.config.SeriesPieData
import myJs.myPkg._
import org.querki.jquery.$
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, ProgressEvent}
import com.karasiq.bootstrap.Bootstrap.default._

import scala.scalajs.js
import scalatags.Text.all._

import scala.math.BigDecimal.RoundingMode
import scala.scalajs.js.{UndefOr, |}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.scalajs.jquery.JQuery
import myJs.myPkg.Implicits._
import shared.Shared


/**
 * Created by yz on 2019/3/6
 */
@JSExportTopLevel("Utils")
object Utils {

  val g = js.Dynamic.global

  val layer = g.layer.asInstanceOf[Layer]

  val checkColumn = ColumnOptions.field("state").checkbox(true)

  implicit class MyJson(val json: js.Dictionary[js.Any]) {

    def myGet(key: String) = json.getOrElse(key, "").toString

    def myGetEmpty(key: String) = {
      val value = json.getOrElse(key, "").toString
      Tool.emptyfy(value)
    }


  }

  def downloadTxt(fileName: String, content: String) = {
    val blob = new Blob(js.Array(content), BlobPropertyBag(`type` = "text/plain;charset=utf-8"))
    g.saveAs(blob, fileName)
  }


  val dataToggle = attr("data-toggle")
  val dataContent = attr("data-content")
  val dataContainer = attr("data-container")
  val dataPlacement = attr("data-placement")
  val dataHtml = attr("data-html")
  val dataAnimation = attr("data-animation")
  val dataTrigger = attr("data-trigger")
  val dataField = attr("data-field")
  val dataSortable = attr("data-sortable")

  def progressHandlingFunction = {
    (e: ProgressEvent) => {
      if (e.lengthComputable) {
        val percent = e.loaded / e.total * 100
        val newPercent = BigDecimal(percent).setScale(2, RoundingMode.HALF_UP)
        $("#progress").html(s"(${newPercent}%)")
        if (percent >= 100) {
          $("#info").text("正在运行")
          $("#progress").html("")
        }
      }
    }

  }

  @JSExport("setColumns")
  def setColumns(value: String, parentStr: String = "") = {
    val element = $(s"input:checkbox[value='${value}']")
    if (element.is(":checked")) {
      $(s"${parentStr} #table").bootstrapTable("showColumn", value)
    } else {
      $(s"${parentStr} #table").bootstrapTable("hideColumn", value)
    }

  }

  def any2undefOr[T](obj: T) = UndefOr.any2undefOrA(obj)

  def seriesCfgData[T](obj: T) = obj.asInstanceOf[UndefOr[js.Array[CleanJsObject[SeriesPieData] | Double]]]


}
