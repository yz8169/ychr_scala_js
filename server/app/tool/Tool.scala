package tool

import java.io.File
import java.nio.file.Files

import controllers.{BasicInfoData, UserData, UserStrData}
import dao._
import javax.inject.Inject
import org.joda.time.{DateTime, Days}
import play.api.libs.json.Json
import play.api.mvc.Results._
import models.Tables._
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import play.api.mvc.RequestHeader
import shared.Shared
import utils.Utils

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.Future
import scala.math.BigDecimal.RoundingMode
import utils.Implicits._

/**
  * Created by yz on 2018/7/30
  */
class Tool @Inject()(loginIpDao: LoginIpDao, validDao: ValidDao, modeDao: ModeDao, strWeightDao: StrWeightDao,
                     basicInfoDao: BasicInfoDao, strDbDao: StrDbDao, userStrDbDao: UserStrDbDao,
                     userSnpDbDao: UserSnpDbDao, snpDataDao: SnpDataDao, strDataDao: StrDataDao) {

  def strInfer(strRow: StrDataRow, data: BasicInfoRow)(implicit request: RequestHeader): Future[Unit] = {
    val startTime = System.currentTimeMillis()
    val userId = getUserId
    basicInfoDao.insertOrUpdate(data).flatMap { x =>
      basicInfoDao.selectByNumberSome(userId, data.number)
    }.flatMap {
      row =>
        val startTime = System.currentTimeMillis()
        case class StrDbMapData(number: String, kind: String, map: Map[String, String])
        strWeightDao.selectAll(0).zip(strDbDao.selectAll).zip(userStrDbDao.selectAll(userId)).
          zip(strDataDao.selectAll(userId)).flatMap { case (((weightRows, strDbs), userStrDbs), dbStrDatas) =>
          val strDataMap = dbStrDatas.map { x =>
            val splitRow = str2Db(x)
            (x.number, splitRow.map)
          }.toMap
          val weightMap = weightRows.map(x => (x.siteName, x.value)).toMap
          val newStrRow = str2Db(strRow)
          val strDbMaps = strDbs.map { row =>
            StrDbMapData(row.number, row.kind, Json.parse(row.value).as[Map[String, String]])
          } ++ userStrDbs.map { row =>
            StrDbMapData(row.number, row.kind, strDataMap(row.number))
          }
          val strDbMap = strDbMaps.map(x => (x.number, x)).toMap
          val distanceInfos = strDbMaps.map { info =>
            val map = info.map
            val tmpDistances = newStrRow.map.toList.map { case (siteName, v) =>
              val data = map.getOrElse(siteName, "")
              val weight = weightMap(siteName)
              (v, data, weight)
            }.withFilter { case (strO, data, z) =>
              StringUtils.isNotBlank(strO) && StringUtils.isNotBlank(data) && z.isDefined
            }.withFilter { case (x, y, z) =>
              Utils.isDouble(x) && Utils.isDouble(y)
            }.map { case (x, y, z) =>
              math.abs(x.toDouble - y.toDouble) * z.get
            }
            (info.kind, info.number, (tmpDistances.sum / tmpDistances.size).formatted("%.3f").toDouble)
          }.sortBy(_._3).take(100)
          val siteNames = Shared.strWeightSiteNames
          val newLines = ArrayBuffer(s"distance\tkind\tindex\t${siteNames.mkString("\t")}") ++= distanceInfos.map { case (kind, index, distance) =>
            val strDbRow = strDbMap(index)
            val map = strDbRow.map
            val datas = siteNames.map { siteName =>
              map.getOrElse(siteName, "")
            }
            s"${distance}\t${kind}\t${index}\t${datas.mkString("\t")}"
          }
          val detailFile = new File(Utils.userDir, s"${userId}/strInfer/${data.number}.txt")
          newLines.toFile(detailFile)
          val distances = distanceInfos.map(x => (x._1, x._3)).groupBy(_._1).mapValues { x => (x.head._1, x.head._2, x.size) }.values.toBuffer
          val inferSnpKitName = (distances).sortBy(_._2).map { case (kind, distance, size) =>
            s"${kind}:${distance},${size}"
          }.mkString(";")
          val newData = row.copy(inferSnpKitName = Some(inferSnpKitName))
          basicInfoDao.update(newData)
        }
    }

  }

  def snpStat(snpRow: SnpDataRow, number: String)(implicit request: RequestHeader): Future[Unit] = {
    val userId = getUserId
    val startTime = System.currentTimeMillis()
    val kind = snpRow.kitName
    case class SnpDbMapData(number: String, map: Map[String, String])
    case class SnpStatRow(number: String, rate: BigDecimal, num: Int, totalNum: Int)
    userSnpDbDao.selectAll(userId, kind).flatMap { userSnpDbs =>
      val ids = userSnpDbs.map(_.number)
      snpDataDao.selectByNumbers(userId, ids).map { dbSnpDatas =>
        val snpDataMap = getSnpDataMap(dbSnpDatas)
        if (userSnpDbs.nonEmpty) {
          val strDbMaps = userSnpDbs.map { row =>
            SnpDbMapData(row.number, snpDataMap(row.number))
          }
          val strDbMap = strDbMaps.map(x => (x.number, x)).toMap
          val distanceInfos = strDbMaps.map { info =>
            val map = info.map
            val calculateMap = Utils.str2Map(snpRow.value)
            val filterRows = calculateMap.toList.map { case (siteName, data) =>
              val dbData = map.getOrElse(siteName, "")
              (data, dbData)
            }.filter { case (data, dbData) =>
              StringUtils.isNotBlank(data) && StringUtils.isNotBlank(dbData)
            }.filter { case (data, dbData) =>
              Tool.is01(data) && Tool.is01(dbData)
            }.filter { case (data, dbData) =>
              data.trim == dbData.trim
            }
            val rate = BigDecimal(filterRows.size) / calculateMap.size
            SnpStatRow(info.number, Utils.scale100(rate, 1), filterRows.size, calculateMap.size)
          }.sortWith(_.rate > _.rate).take(100)
          val firstMap = strDbMaps.head.map
          val siteNames = firstMap.keySet.toBuffer
          val newLines = ArrayBuffer(s"rate\tindex\t${siteNames.mkString("\t")}") ++= distanceInfos.map { row =>
            val strDbRow = strDbMap(row.number)
            val map = strDbRow.map
            val datas = siteNames.map { siteName =>
              map(siteName)
            }
            s"${row.rate}(${row.num}/${row.totalNum})\t${row.number}\t${datas.mkString("\t")}"
          }
          val detailFile = Tool.getSnpStatFile(userId, number)
          newLines.toFile(detailFile)
        }
      }


    }
  }

  def getSnpDataMap(dbSnpDatas: Seq[SnpDataRow]) = {
    dbSnpDatas.map { x =>
      val map = Utils.str2Map(x.value)
      (x.number, map)
    }.toMap

  }

  def getStrDataMap(dbStrDatas: Seq[StrDataRow]) = {
    dbStrDatas.map { x =>
      val map = Utils.str2Map(x.value)
      (x.number, map)
    }.toMap

  }

  def getStrInferFile(userId: Int, number: String) = {
    new File(Utils.userDir, s"${userId}/strInfer/${number}.txt")
  }

  def strWeightSplit(row: UserStrData) = {
    val map = row.map
    val convertSiteNames = ArrayBuffer("DYS385a/b", "DYF387S1", "DYS527a/b", "DYS404S1a/b")
    val newMap = map.flatMap { case (siteName, v) =>
      val values = if (convertSiteNames.contains(siteName)) {
        val values = v.split(",")
        values.size match {
          case 1 => Array(values(0), values(0))
          case 2 => values
          case _ => Array("")
        }
      } else Array(v)
      val siteNames = siteName match {
        case "DYS385a/b" => Array("DYS385a", "DYS385b")
        case "DYF387S1" => Array("DYS387a", "DYS387b")
        case "DYS527a/b" => Array("DYS527a", "DYS527b")
        case "DYS404S1a/b" =>
          Array("DYS404S1a", "DYS404S1b")
        case x => Array(x)
      }
      siteNames.zip(values).map { case (siteName, value) =>
        (siteName, value)
      }
    }
    row.copy(map = newMap)

  }

  def str2Db(x: StrDataRow) = {
    val map = Utils.str2Map(x.value)
    val data = UserStrData(x.kitName, map)
    val unifiyRow = Tool.strSiteNameUnify(data)
    strWeightSplit(unifiyRow)
  }

  def validLoginIp(user: UserData, ip: String) = {
    if (phoneValid) {
      loginIpDao.selectByName(user.name).map { dbLoginIps =>
        val loginIpOption = dbLoginIps.filter(_.ip == ip).headOption
        if (loginIpOption.isDefined) {
          val currentTime = new DateTime()
          val loginIp = loginIpOption.get
          val days = Days.daysBetween(loginIp.time, currentTime).getDays
          if (days > 30) {
            Ok(Json.obj("isValid" -> "true", "phone" -> user.phone))
          } else {
            Ok(Json.obj("isValid" -> "false"))
          }
        } else {
          Ok(Json.obj("isValid" -> "true", "phone" -> user.phone))
        }
      }
    } else {
      Utils.result2Future(Ok(Json.obj("isValid" -> "false")))
    }

  }

  def initWeight(userId: Int) = {
    strWeightDao.selectAll(userId).flatMap { x =>
      if (x.isEmpty) {
        strWeightDao.selectAll(0).flatMap { x =>
          val rows = x.map { row => row.copy(userId = userId) }
          strWeightDao.insertAll(rows).map(_ => rows)
        }
      } else {
        Future {
          x
        }
      }
    }

  }

  def insertOrUpdateLoginIp(name: String, ip: String) = {
    loginIpDao.selectByNameAndIp(name, ip).flatMap { dbLoginIp =>
      val row = dbLoginIp.map { loginIp =>
        LoginIpRow(loginIp.id, name, ip, new DateTime())
      }.getOrElse(LoginIpRow(0, name, ip, new DateTime()))
      loginIpDao.insertOrUpdate(row)
    }
  }

  def phoneValid = {
    Utils.execFuture(validDao.selectByName("phone")).value == "t"
  }

  def getUserId(implicit request: RequestHeader) = {
    request.session.get("userId").get.toInt
  }

  def createTempDirectory(prefix: String) = {
    if (isTestMode) Utils.testDir else Files.createTempDirectory(prefix).toFile
  }

  def isTestMode = {
    val mode = Utils.execFuture(modeDao.select)
    if (mode.test == "t") true else false
  }

  def deleteDirectory(direcotry: File) = {
    if (!isTestMode) Utils.deleteDirectory(direcotry)
  }
}

object Tool {

  def dat2txt(datFile: File, txtFile: File) = {
    val lines = datFile.lines
    val map = mutable.Map[String, ArrayBuffer[String]]()
    var key = ""
    val symbol = lines(21).trim
    lines.zipWithIndex.foreach { case (line, i) =>
      if (line == "DNA Analysis Result") {
        key = lines(i + 3)
        map(key) = ArrayBuffer[String]()
      } else if (map.isDefinedAt(key)) {
        map(key) += line
      }
    }
    val newLines = ArrayBuffer(s"Sample Name\tMarker\t")

    newLines ++= map.flatMap { case (sampleName, lines) =>
      lines.zipWithIndex.filter { case (line, i) =>
        line == symbol
      }.map { case (line, i) =>
        val siteName = lines(i - 2)
        val valueNumber = lines(i + 3).toInt
        val value = (1 to valueNumber).map { valueI =>
          lines(i + 3 + valueI)
        }.mkString("\t")
        s"${sampleName}\t${siteName}\t${value}"
      }
    }
    newLines.toFile(txtFile)
  }

  def strSiteNameUnify(row: UserStrData) = {
    val strKitName = row.kitName
    val strSiteNameMap = Shared.strSiteNameDatas.find(_.kitName == strKitName).get.otherNameMap
    val map = row.map
    val newMap = map.map { case (siteName, v) =>
      val newSiteName = strSiteNameMap.getOrElse(siteName, siteName)
      (newSiteName, v)
    }
    row.copy(map = newMap)
  }

  def getSnpStatFile(userId: Int, number: String) = {
    new File(Utils.userDir, s"${userId}/snpStat/${number}.txt")
  }

  def is01(value: String) = value == "0" || value == "1"

  def deleteSnpStatFile(userId: Int, number: String) = {
    val snpStatFile = Tool.getSnpStatFile(userId, number)
    Utils.deleteFile(snpStatFile)
  }

}
