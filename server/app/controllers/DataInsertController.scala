package controllers

import java.io.File

import dao._
import javax.inject.Inject
import models.Tables.{StrDbRow, StrWeightRow}
import org.apache.commons.io.FileUtils
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import shared.Shared
import tool.Tool
import utils.Utils

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import utils.Implicits._


/**
  * Created by yz on 2018/11/22
  */
class DataInsertController @Inject()(cc: ControllerComponents, tool: Tool, strWeightDao: StrWeightDao,
                                     strDbDao: StrDbDao) extends AbstractController(cc) {

  val parent = new File("E:\\ychr\\test")

  def insertStrWeight = Action.async { implicit request =>
    val weights = ArrayBuffer(4, 3, 2, 4, 3, 20, 8, 7, 27, 2, 5, 2, 1, 2, 3, 1, 0.3, 9, 20)
    val file = new File(parent, "info.txt")
    val lines = file.lines
    val infoHeaders = lines.head.split("\t").drop(2)
    val weightMap = infoHeaders.zip(weights).toMap
    val siteNames = Shared.strWeightSiteNames
    val rows = siteNames.map { siteName =>
      val value = weightMap.get(siteName)
      StrWeightRow(0, siteName, value)
    }
    strWeightDao.deleteAll.flatMap(_ => strWeightDao.insertAll(rows)).map { x =>
      Ok("success!")
    }

  }

  def insertStrDb = Action { implicit request =>
    val file = new File(parent, "姓氏民族地理.xlsx")
    val otherLines = Utils.xlsx2Lines(file)
    val nameMap = Utils.lines2columns(otherLines).map { columns =>
      (columns(0), columns(1))
    }.toMap
    val sheet2Lines = Utils.xlsx2Lines(file, 1)
    case class NAData(nation: String, address: String)
    val naMap = Utils.lines2columns(sheet2Lines).map { columns =>
      (columns(0), NAData(columns(1), columns(2)))
    }.toMap

    val dataFile = new File(parent, "data_info.txt")
    val lines = dataFile.lines
    val headers = lines.head.split("\t")
    val rows = lines.drop(1).map { line =>
      val columns = line.split("\t").padTo(headers.size, "")
      val map = columns.drop(2).zipWithIndex.map { case (value, i) =>
        (headers(i + 2), value)
      }.toMap
      val key = columns(1)
      val na = naMap.getOrElse(key, NAData("", ""))
      StrDbRow("HGT" + columns(1), columns(0), Json.toJson(map).toString(), nameMap.getOrElse(key, ""), na.nation, na.address)
    }

    Await.result(strDbDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(strDbDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

}
