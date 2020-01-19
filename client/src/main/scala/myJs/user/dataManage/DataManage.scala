package myJs.user.dataManage

import com.highcharts._

import scalajs.js
import js.UndefOr
import com.highcharts.HighchartsAliases._
import com.highcharts.HighchartsUtils._
import com.highcharts.config._
import com.highcharts.config.Tooltip
import myJs.{Tool, Utils}
import myJs.Tool.{element, layerOptions, zhInfo}
import myJs.Utils._
import myJs.myPkg._
import myJs.user.snp.InferDetail
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared
import com.karasiq.bootstrap.Bootstrap.default._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.{JSON, UndefOr}
import myJs.myPkg.Implicits

import scala.scalajs.js.JSConverters._
import myJs.myPkg.Implicits._
import org.scalajs.dom
import org.scalajs.dom.raw._

import scala.collection.mutable.ArrayBuffer

/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("DataManage")
object DataManage {

  var confirmIndex: Int = _
  var index: Int = _
  val parentStr = "#addStrModal"
  val snpParentStr = "#addSnpModal"
  val snpStatParentStr = "#snpStatModal"
  val strStatParentStr = "#strStatModal"
  val ids = ArrayBuffer[String]()

  @JSExport("init")
  def init = {
    dateInit
    refreshTable(() =>
      bindEvt, showLoading = true
    )

  }

  @JSExport("expand")
  def expand(y: Element) = {
    val tool = $(y).parent().find(".tools a:last")
    $(tool).click()

  }

  @JSExport("deleteAllInfos")
  def deleteAllInfos = {
    val options = SwalOptions.title("").text("确定要删除选中的数据吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消")
    Swal.swal(options, () => {
      val data = js.Dictionary(
        "numbers" -> ids.toJSArray
      )
      val url = g.jsRoutes.controllers.UserDataController.deleteDataByNumbers().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
        `type`("post").data(JSON.stringify(data)).success { (data, status, e) =>
        refreshTable { () =>
          Swal.swal(SwalOptions.title("成功").text("删除成功！").`type`("success"))
          getIds
        }

      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("删除失败！").`type`("error"))
      }
      $.ajax(ajaxSettings)
    })

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

  def bindEvt = {
    $("#table").on("check.bs.table", () => getIds)
    $("#table").on("uncheck.bs.table", () => getIds)
    $("#table").on("check-all.bs.table", () => getIds)
    $("#table").on("uncheck-all.bs.table", () => getIds)
    $("#table").on("page-change.bs.table", () => getIds)

  }

  @JSExport("view")
  def view(number: String) = {
    $("#strStatModal #number").text(number)
    viewName(number)
    viewNation(number)
    viewAddress(number)
    StrInferDetail.refreshTable(number)
    jQuery(strStatParentStr).modal("show")

  }

  @JSExport("snpView")
  def snpView(number: String) = {
    val url = g.jsRoutes.controllers.UserDataController.getSnpInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val array = data.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      $("#snpStatModal #number").text(number)
      viewSnpNation(array)
      viewSnpAddress(array)
      viewSnpName(array)
      InferDetail.refreshTable(number)
      jQuery(snpStatParentStr).modal("show")
    }
    $.ajax(ajaxSettings)

  }

  def notEmpty(x: String) = x != "" && x != ""

  def viewName(number: String) = {
    val url = g.jsRoutes.controllers.UserDataController.getNameInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val array = rs("array").asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val plotData = array.map { x =>
        SeriesPieData(y = x.myGet("num").toDouble, name = x.myGet("name"))
      }
      val plotConfig = getPiePlotConfig(plotData, myTitle = "姓氏统计")
      jQuery(s"${strStatParentStr} #name #container").highcharts(plotConfig)
      $(s"${strStatParentStr} #nameTable").bootstrapTable("load", rs("array")).
        bootstrapTable("selectPage", 1)
      $(s"${strStatParentStr} #name #recordsNum").text(rs.myGet("recordNum"))
    }
    $.ajax(ajaxSettings)

  }

  def viewSnpName(array: js.Array[js.Dictionary[js.Any]]) = {
    val names = array.map(x => x.myGet("name")).filter(x => notEmpty(x)).map { x =>
      Shared.getLsName(x)
    }
    case class Info(percent: BigDecimal, num: Int)
    val infos = names.groupBy(x => x).mapValues { values =>
      val value = BigDecimal(values.size) / names.size
      val percent = Shared.scale100(value, 1)
      Info(percent, values.size)
    }.toList.sortWith((x, y) => x._2.percent > y._2.percent).map { case (key, info) =>
      js.Dictionary[js.Any]("name" -> key, "percent" -> s"${info.percent}%", "num" -> info.num)
    }.toJSArray
    val plotData = infos.map { x =>
      SeriesPieData(y = x.myGet("num").toDouble, name = x.myGet("name"))
    }
    val parentStr = "#snpName"
    val plotConfig = getPiePlotConfig(plotData, myTitle = "姓氏统计")
    jQuery(s"${parentStr} #container").highcharts(plotConfig)
    $(s"${parentStr} #nameTable").bootstrapTable().bootstrapTable("load", infos).
      bootstrapTable("selectPage", 1)
    $(s"${parentStr} #recordsNum").text(names.size.toString)

  }

  def viewNation(number: String) = {
    val str = s"#myNation"
    val url = g.jsRoutes.controllers.UserDataController.getNationInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val array = rs("array").asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val plotData = array.map { x =>
        SeriesPieData(y = x.myGet("num").toDouble, name = x.myGet("nation"))
      }
      val plotConfig = getPiePlotConfig(plotData, myTitle = "民族统计")
      jQuery(s"${str} #container").highcharts(plotConfig)
      $(s"${str} #nationTable").bootstrapTable("load", rs("array")).
        bootstrapTable("selectPage", 1)
      $(s"${str} #recordsNum").text(rs.myGet("recordNum"))
    }
    $.ajax(ajaxSettings)

  }

  def viewSnpNation(array: js.Array[js.Dictionary[js.Any]]) = {
    val names = array.map(x => x.myGet("nation")).filter(x => notEmpty(x))
    case class Info(percent: BigDecimal, num: Int)
    val infos = names.groupBy(x => x).mapValues { values =>
      val value = BigDecimal(values.size) / names.size
      val percent = Shared.scale100(value, 1)
      Info(percent, values.size)
    }.toList.sortWith((x, y) => x._2.percent > y._2.percent).map { case (key, info) =>
      js.Dictionary[js.Any]("nation" -> key, "percent" -> s"${info.percent}%", "num" -> info.num)
    }.toJSArray
    val plotData = infos.map { x =>
      SeriesPieData(y = x.myGet("num").toDouble, name = x.myGet("nation"))
    }
    val parentStr = "#snpNation"
    val plotConfig = getPiePlotConfig(plotData, myTitle = "民族统计")
    jQuery(s"${parentStr} #container").highcharts(plotConfig)
    $(s"${parentStr} #nationTable").bootstrapTable().bootstrapTable("load", infos).
      bootstrapTable("selectPage", 1)
    $(s"${parentStr} #recordsNum").text(names.size.toString)

  }

  def viewAddress(number: String) = {
    val url = g.jsRoutes.controllers.UserDataController.getAdressInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val array = rs("array").asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val plotData = array.map { x =>
        SeriesPieData(y = x.myGet("num").toDouble, name = x.myGet("address"))
      }
      val plotConfig = getPiePlotConfig(plotData, myTitle = "地理统计")
      jQuery("#address #container").highcharts(plotConfig)
      $("#addressTable").bootstrapTable("load", rs("array")).
        bootstrapTable("selectPage", 1)
      $("#address #recordsNum").text(rs.myGet("recordNum"))
    }
    $.ajax(ajaxSettings)

  }

  def viewSnpAddress(array: js.Array[js.Dictionary[js.Any]]) = {
    val names = array.map(x => x.myGet("residentialPlace")).filter(x => notEmpty(x)).map { x =>
      Shared.getCity(x)
    }.filter(_ != "")
    case class Info(percent: BigDecimal, num: Int)
    val infos = names.groupBy(x => x).mapValues { values =>
      val value = BigDecimal(values.size) / names.size
      val percent = Shared.scale100(value, 1)
      Info(percent, values.size)
    }.toList.sortWith((x, y) => x._2.percent > y._2.percent).map { case (key, info) =>
      js.Dictionary[js.Any]("address" -> key, "percent" -> s"${info.percent}%", "num" -> info.num)
    }.toJSArray
    val plotData = infos.map { x =>
      SeriesPieData(y = x.myGet("num").toDouble, name = x.myGet("address"))
    }
    val parentStr = "#snpAddress"
    val plotConfig = getPiePlotConfig(plotData, myTitle = "地理统计")
    jQuery(s"${parentStr} #container").highcharts(plotConfig)
    $(s"${parentStr} #addressTable").bootstrapTable().bootstrapTable("load", infos).
      bootstrapTable("selectPage", 1)
    $(s"${parentStr} #recordsNum").text(names.size.toString)

  }

  @JSExport("snpRefresh")
  def snpRefresh(number: String) = {
    val index = layer.alert(element, layerOptions)
    val url = g.jsRoutes.controllers.UserSnpDataController.refreshSnpStat().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").
      `type`("get").contentType("application/json").success { (data, status, e) =>
      layer.close(index)
      Swal.swal(SwalOptions.title("成功").text("刷新成功").`type`("success"))
    }.error { (data, status, e) =>
      Swal.swal(SwalOptions.title("错误").text("刷新失败").`type`("error"))
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("strRefresh")
  def strRefresh(number: String) = {
    val index = layer.alert(element, layerOptions)
    val url = g.jsRoutes.controllers.UserStrDataController.refreshStrInfer().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").
      `type`("get").contentType("application/json").success { (data, status, e) =>
      refreshTable { () =>
        layer.close(index)
        Swal.swal(SwalOptions.title("成功").text("刷新成功").`type`("success"))
      }

    }.error { (data, status, e) =>
      Swal.swal(SwalOptions.title("错误").text("刷新失败").`type`("error"))
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("detailFmt")
  def detailFmt: js.Function = {
    (v: js.Any, row: js.Dictionary[js.Any]) =>
      val addStr = a(
        title := "入库",
        onclick := s"DataManage.add2DbBefore(${JSON.stringify(row)})",
        cursor.pointer,
        span(
          em(cls := "fa fa-plus")
        )
      ).render
      val removeStr = a(
        title := "出库",
        onclick := s"DataManage.strDelete('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-remove")
        )
      ).render
      val updateStr = a(title := "修改",
        onclick := s"DataManage.updateUserStr('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-edit")
        )
      ).render
      val b = row.get("kind").isDefined
      val storeStr = if (b) ArrayBuffer(removeStr, updateStr) else ArrayBuffer(addStr)
      val nameStr = a(title := "详情",
        onclick := s"DataManage.view('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-table")
        )
      ).render
      val refreshStr = a(title := "刷新",
        onclick := s"DataManage.strRefresh('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-refresh")
        )
      ).render

      if (row.myGet("strKitName") == "") {
        "无STR数据信息，无法推断"
      } else {
        (ArrayBuffer(refreshStr) ++= storeStr += nameStr).mkString("&nbsp;")
      }

  }

  @JSExport("inferFmt")
  def inferFmt: js.Function = {
    (v: String, row: js.Dictionary[js.Any]) =>
      if (v != "") {
        val lines = v.split(";")
        lines.zipWithIndex.map { case (x, i) =>
          val values = x.split(":")
          val html = if (values(0) != "limit") {
            val currKind = values(0)
            val kindOp = row.get("kind")
            val showContent = s"${currKind}(${values(1)})"
            kindOp match {
              case Some(kind) => if (currKind == kind) {
                span(color := "green", showContent).render
              } else showContent
              case None => showContent
            }
          } else values(1)
          val op = if (i < lines.length - 1) {
            val nextLine = lines(i + 1).split(":")
            val nextValue = nextLine(1).split(",")(0).toDouble
            val currValue = values(1).split(",")(0).toDouble
            if (nextValue == currValue) {
              " = "
            } else " < "
          } else ""
          html + op
        }.mkString("&nbsp;")
      } else ""

  }

  @JSExport("snpInferFmt")
  def snpInferFmt: js.Function = {
    (v: js.Any, row: js.Dictionary[js.Any]) =>
      val addStr = a(
        title := "入库",
        onclick := s"DataManage.addSnp2DbBefore(${JSON.stringify(row)})",
        cursor.pointer,
        span(
          em(cls := "fa fa-plus")
        )
      )
      val removeStr = a(
        title := "出库",
        onclick := s"DataManage.snpDelete('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-remove")
        )
      )
      val b = row("inSnpStore").asInstanceOf[Boolean]
      val storeStr = if (b) removeStr else addStr
      val nameStr = a(title := "详情",
        onclick := s"DataManage.snpView('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-table")
        )
      )
      val refreshStr = a(title := "刷新",
        onclick := s"DataManage.snpRefresh('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-refresh")
        )
      )

      if (row.myGet("snpKitName") == "") {
        "无SNP数据信息，无法推断"
      } else {
        Array(refreshStr, storeStr, nameStr).mkString("&nbsp;")
      }

  }

  @JSExport("search")
  def search = {
    val parentStr = "#search"
    val data = $(s"${parentStr} #form").serialize()
    val url = g.jsRoutes.controllers.UserDataController.search().url.toString
    val index = layer.alert(element, layerOptions)
    val ajaxSettings = JQueryAjaxSettings.url(url).
      `type`("post").data(data).success { (data, status, e) =>
      $("#table").bootstrapTable("destroy").bootstrapTable(TableOptions.data(data))
      layer.close(index)

    }
    $.ajax(ajaxSettings)

  }

  def dateInit = {
    val options = DatepickerOptions.format(Tool.pattern).language("zh-CN")
    $(".datepicker").datepicker(options)
  }

  def refreshTable(f: () => js.Any = () => (), showLoading: Boolean = false) = {
    val index = if (showLoading) layer.alert(Tool.loadingElement, Tool.layerOptions) else 0
    val url = g.jsRoutes.controllers.UserDataController.getAllBasicInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val array = data.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val units = array.map { x =>
        x.myGet("unit")
      }.distinct
      val tbOptions = TableOptions
      $("#table").bootstrapTable(tbOptions)
      $("#table").bootstrapTable("load", data)
      val sampleTypes = js.Array("血斑/血液", "唾液斑", "其他")
      val sexs = array.map { x => x.myGet("sex") }.map(x => Shared.nafy(x)).distinct
      val countries = array.map { x => x.myGet("country") }.map(x => Shared.nafy(x)).distinct
      val nations = array.map { x => x.myGet("nation") }.map(x => Shared.nafy(x)).distinct
      val options = Select2Options.placeholder("--请选择--").data(units).
        allowClear(true)
      $(s"#search :input[name='units[]']").empty().select2(options)
      $(s"#search :input[name='sampleTypes[]']").empty().select2(Select2Options.placeholder("--请选择--").data(sampleTypes).
        allowClear(true).multiple(true))
      $(s"#search :input[name='sexs[]']").empty().select2(Select2Options.placeholder("--请选择--").data(sexs).
        allowClear(true).multiple(true))
      $(s"#search :input[name='countries[]']").empty().select2(Select2Options.placeholder("--请选择--").data(countries).
        allowClear(true).multiple(true))
      $(s"#search :input[name='nations[]']").empty().select2(Select2Options.placeholder("--请选择--").data(nations).
        allowClear(true).multiple(true))
      layer.close(index)
      f()
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("snpDelete")
  def snpDelete(number: String) = {
    val options = SwalOptions.title("").text("确定要将此数据从库中移除吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消")
    Swal.swal(options, () => {
      val url = g.jsRoutes.controllers.UserSnpDataController.deleteDataByNumber().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").
        `type`("get").contentType("application/json").success { (data, status, e) =>
        refreshTable { () =>
          Swal.swal(SwalOptions.title("成功").text("移除成功").`type`("success"))
        }
      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("移除失败").`type`("error"))
      }
      $.ajax(ajaxSettings)
    })

  }

  @JSExport("strDelete")
  def strDelete(number: String) = {
    val options = SwalOptions.title("").text("确定要将此数据从库中移除吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消")
    Swal.swal(options, () => {
      val url = g.jsRoutes.controllers.UserStrDataController.deleteDataByNumber().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").
        `type`("get").contentType("application/json").success { (data, status, e) =>
        refreshTable { () =>
          Swal.swal(SwalOptions.title("成功").text("移除成功").`type`("success"))
        }
      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("移除失败").`type`("error"))
      }
      $.ajax(ajaxSettings)
    })

  }

  @JSExport("strAddBatch")
  def strAddBatch = {
    val index = layer.alert(element, layerOptions)
    val url = g.jsRoutes.controllers.UserStrDataController.addBatch().url.toString
    val data = js.Dictionary(
      "numbers" -> ids.toJSArray
    )
    val ajaxSettings = JQueryAjaxSettings.url(url).data(JSON.stringify(data)).
      `type`("post").contentType("application/json").success { (data, status, e) =>
      refreshTable { () =>
        layer.close(index)
        val options = SwalOptions.`type`("success").title("成功!").text("添加成功!")
        Swal.swal(options)
        getIds
      }

    }
    $.ajax(ajaxSettings)

  }

  @JSExport("snpAddBatch")
  def snpAddBatch = {
    val index = layer.alert(element, layerOptions)
    val url = g.jsRoutes.controllers.UserSnpDataController.addBatch().url.toString
    val data = js.Dictionary(
      "numbers" -> ids.toJSArray
    )
    val ajaxSettings = JQueryAjaxSettings.url(url).data(JSON.stringify(data)).
      `type`("post").contentType("application/json").success { (data, status, e) =>
      refreshTable { () =>
        layer.close(index)
        val options = SwalOptions.`type`("success").title("成功!").text("添加成功!")
        Swal.swal(options)
        getIds
      }

    }
    $.ajax(ajaxSettings)

  }

  @JSExport("strBatchRefresh")
  def strBatchRefresh = {
    val data = js.Dictionary(
      "numbers" -> ids.toJSArray
    )
    val str = JSON.stringify(data)
    val wsUri = g.jsRoutes.controllers.UserStrDataController.batchRefresh().webSocketURL().toString
    webSocket(str, wsUri)

  }

  @JSExport("snpBatchRefresh")
  def snpBatchRefresh = {
    val data = js.Dictionary(
      "numbers" -> ids.toJSArray
    )
    val str = JSON.stringify(data)
    val wsUri = g.jsRoutes.controllers.UserSnpDataController.batchRefresh().webSocketURL().toString
    webSocket(str, wsUri)

  }

  @JSExport("downloadData")
  def downloadData = {
    val data = js.Dictionary(
      "numbers" -> ids.toJSArray
    )
    val url = g.jsRoutes.controllers.UserDataController.getDownloadData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("post").data(JSON.stringify(data)).success { (data, status, e) =>
      val content = data.toString
      Utils.downloadTxt("data.txt", content)
    }.error { (data, status, e) =>
      Swal.swal(SwalOptions.title("错误").text("错误！").`type`("error"))
    }
    $.ajax(ajaxSettings)

  }

  def webSocket(data: String, wsUri: String) = {
    val index = layer.alert(Tool.element, layerOptions)
    val websocket = new WebSocket(wsUri)

    def onOpen(evt: Event) = {
      doSend(data)

    }

    def onMessage(evt: MessageEvent) = {
      val str = evt.data.toString
      val data = JSON.parse(str).asInstanceOf[js.Dictionary[String]]
      $("#progress").html(s"${data("message")}")

    }

    def doSend(message: String) = {
      websocket.send(message)

    }

    def onClose(evt: CloseEvent) = {
      refreshTable { () =>
        layer.close(index)
        val options = SwalOptions.`type`("success").title("成功").text("刷新成功！")
        Swal.swal(options)
        getIds
      }


    }

    websocket.onopen = (evt: Event) => onOpen(evt)
    websocket.onmessage = (evt: MessageEvent) => onMessage(evt)
    websocket.onclose = (evt: CloseEvent) => onClose(evt)

  }


  @JSExport("add2DbBefore")
  def add2DbBefore(row: js.Dictionary[js.Any]) = {
    $(s"${parentStr} input[name='number']").`val`(row.myGet("number"))
    val value = row.myGet("snpKitName")
    if (value != "" && value != "coreset") {
      $(s"${parentStr} select[name='kind']").`val`(value)
    } else {
      val panel = row.myGet("inferSnpKitName").split(";")(0).split(":")(0)
      $(s"${parentStr} select[name='kind']").`val`(panel)
    }
    jQuery(parentStr).modal("show")

  }


  @JSExport("addSnp2DbBefore")
  def addSnp2DbBefore(row: js.Dictionary[js.Any]) = {
    $(s"${snpParentStr} input[name='number']").`val`(row.myGet("number"))
    $(s"${snpParentStr} input[name='kind']").`val`(row.myGet("snpKitName"))
    jQuery(snpParentStr).modal("show")

  }


  @JSExport("addSnp2Db")
  def addSnp2Db = {
    index = layer.alert(element, layerOptions)
    val url = g.jsRoutes.controllers.UserSnpDataController.numberCheck().url.toString
    val data = $(s"${snpParentStr} #form").serialize()
    val ajaxSettings = JQueryAjaxSettings.url(url).data(data).
      `type`("get").success { (data, status, e) =>
      layer.close(index)
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val b = rs("valid").asInstanceOf[Boolean]
      if (b) {
        addSnp2DbRun
      } else {
        val confirmOptions = LayerOptions.btn(js.Array("覆盖", "取消"))
        confirmIndex = layer.confirm("该样本编号已存在，是否覆盖？", confirmOptions, () => addSnp2DbRun, () => ())
      }
    }
    $.ajax(ajaxSettings)

  }

  def addSnp2DbRun = {
    val data = $(s"${snpParentStr} #form").serialize()
    val url = g.jsRoutes.controllers.UserSnpDataController.add2Db().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).data(data).
      `type`("post").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val b = rs.get("valid").map(x => x.asInstanceOf[Boolean]).getOrElse(false)
      if (b) {
        val options = SwalOptions.title("Error").`type`("error").text(rs("message"))
        Swal.swal(options)
      } else {
        layer.close(confirmIndex)
        jQuery(s"${snpParentStr}").modal("hide")
        refreshTable { () =>
          val options = SwalOptions.`type`("success").title("成功!").text("添加成功")
          Swal.swal(options)
        }
      }
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("operateFmt")
  def operateFmt: js.Function = {
    (v: js.Any, row: js.Dictionary[js.Any]) =>
      val updateStr = a(title := "修改",
        onclick := s"DataManage.updateInfo('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-edit")
        )
      )
      val deleteStr = a(title := "删除",
        onclick := s"deleteData('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-close")
        )
      )
      Array(updateStr, deleteStr).mkString("&nbsp;")

  }

  @JSExport("numberFmt")
  def numberFmt: js.Function = {
    (v: String, row: js.Dictionary[js.Any]) =>
      a(title := "详细信息",
        onclick := s"DataManage.detailInfo('${row("number")}')",
        cursor.pointer,
        v
      ).render

  }

  @JSExport("detailInfo")
  def detailInfo(number: String) = {
    DetailInfo.fillValue(number)
    jQuery("#detailInfoModal").modal("show")

  }

  @JSExport("updateInfo")
  def updateInfo(number: String) = {
    UpdateInfo.fillValue(number)
    jQuery("#updateInfoModal").modal("show")

  }

  @JSExport("updateUserStr")
  def updateUserStr(number: String) = {
    UpdateUserStr.fillValue(number)
    jQuery("#updateUserStrModal").modal("show")

  }


  def getPiePlotConfig(myData: js.Array[SeriesPieData], myTitle: String) = {
    new HighchartsConfig {
      override val chart: UndefOr[CleanJsObject[Chart]] = Chart(`type` = "pie", plotBackgroundColor = null,
        plotBorderWidth = null, plotShadow = false)

      override val title: UndefOr[CleanJsObject[Title]] = Title(text = myTitle)

      override val tooltip: UndefOr[CleanJsObject[Tooltip]] = Tooltip(pointFormat =
        "{point.name}: <b>{point.percentage:.1f}%</b>", useHTML = true, headerFormat = "")

      override val series = js.Array[AnySeries](
        SeriesPie(name = "Brands", data = seriesCfgData(myData))
      )

      override val plotOptions: UndefOr[CleanJsObject[PlotOptions]] = PlotOptions(pie =
        PlotOptionsPie(allowPointSelect = true, cursor = "pointer", dataLabels = PlotOptionsPieDataLabels(
          enabled = true,
          format = "<b>{point.name}</b>: {point.percentage:.1f} %",
          useHTML = true
        )))

      override val credits: UndefOr[CleanJsObject[Credits]] = Credits(enabled = false)

    }

  }


}
