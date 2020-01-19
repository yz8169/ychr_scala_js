package utils

import java.io.{File, FileInputStream}
import java.lang.reflect.Field
import java.net.{InetAddress, NetworkInterface}
import java.sql.Date
import java.text.SimpleDateFormat

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.monitorjbl.xlsx.StreamingReader
import org.apache.commons.io.{FileUtils, IOUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.{Cell, CellType, DateUtil, Row}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import play.api.mvc.{RequestHeader, Result}
import shared.Shared

import scala.math.BigDecimal.RoundingMode
import scala.util.Random
//import org.apache.commons.math3.stat.StatUtils
//import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
//import org.saddle.io._
//import CsvImplicits._
//import javax.imageio.ImageIO
import org.apache.commons.codec.binary.Base64
//import org.apache.pdfbox.pdmodel.PDDocument
//import org.apache.pdfbox.rendering.PDFRenderer
import play.api.libs.json.Json

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import utils.Implicits._
//import scala.math.log10

object Utils {

  val windowsPath = "E:\\ychr_database"
  val playPath = "/root/projects/play"
  val linuxPath = playPath + "/ychr_database"
  val isWindows = {
    if (new File(windowsPath).exists()) true else false
  }

  val orthoMclStr = "orthoMcl"
  val mummerStr = "mummer"
  val ardbStr = "ardb"
  val cazyStr = "cazy"


  val path = {
    if (new File(windowsPath).exists()) windowsPath else linuxPath
  }
  val examplePath = new File(path, "example")
  val dataFile = new File(path, "data")
  val proteinDir = new File(dataFile, "protein")

  val binPath = new File(path, "bin")
  val anno = new File(binPath, "Anno")
  val orthoMcl = new File(binPath, "ORTHOMCLV1.4")
  val pipeLine = new File("/mnt/sdb/linait/pipeline/MicroGenome_pipeline/MicroGenome_pipeline_v3.0")
  val mummer = new File("/mnt/sdb/linait/tools/quickmerge/MUMmer3.23/")
  val blastFile = new File(binPath, "ncbi-blast-2.6.0+/bin/blastn")
  val blastpFile = new File(binPath, "ncbi-blast-2.6.0+/bin/blastp")
  val blastxFile = new File(binPath, "ncbi-blast-2.6.0+/bin/blastx")
  val blast2HtmlFile = new File(binPath, "blast2html-82b8c9722996/blast2html.py")
  val svBin = new File("/mnt/sdb/linait/pipeline/MicroGenome_pipeline/MicroGenome_pipeline_v3.0/src/SV_finder_2.2.1/bin/")
  val rPath = {
    val rPath = "D:\\workspaceForIDEA\\p3bacter\\rScripts"
    val linuxRPath = linuxPath + "/rScripts"
    if (new File(rPath).exists()) rPath else linuxRPath
  }
  val userDir = new File(path, "user")

  val windowsTestDir = new File("G:\\temp")
  val linuxTestDir = new File(playPath, "workspace")
  val testDir = if (windowsTestDir.exists()) windowsTestDir else linuxTestDir
  val crisprDir = s"${playPath}/../perls/CRISPRCasFinder"
  val vmatchDir = s"${playPath}/../perls/vmatch-2.3.0-Linux_x86_64-64bit"

  val scriptHtml =
    """
      |<script>
      |	$(function () {
      |			    $("footer:first").remove()
      |        $("#content").css("margin","0")
      |       $(".linkheader>a").each(function () {
      |				   var text=$(this).text()
      |				   $(this).replaceWith("<span style='color: #222222;'>"+text+"</span>")
      |			   })
      |
      |      $("tr").each(function () {
      |         var a=$(this).find("td>a:last")
      |					var text=a.text()
      |					a.replaceWith("<span style='color: #222222;'>"+text+"</span>")
      |				})
      |
      |       $("p.titleinfo>a").each(function () {
      |				   var text=$(this).text()
      |				   $(this).replaceWith("<span style='color: #606060;'>"+text+"</span>")
      |			   })
      |
      |       $(".param:eq(1)").parent().hide()
      |       $(".linkheader").hide()
      |
      |			})
      |</script>
    """.stripMargin

  val Rscript = {
    "Rscript"
  }

  val pyPath = {
    val path = "D:\\workspaceForIDEA\\p3bacter\\pyScripts"
    val linuxPyPath = linuxPath + "/pyScripts"
    if (new File(path).exists()) path else linuxPyPath
  }

  val goPy = {
    val path = "C:\\Python\\python.exe"
    if (new File(path).exists()) path else "python"
  }

  val pyScript =
    """
      |<script>
      |Plotly.Plots.resize(document.getElementById($('#charts').children().eq(0).attr("id")));
      |window.addEventListener("resize", function (ev) {
      |				Plotly.Plots.resize(document.getElementById($('#charts').children().eq(0).attr("id")));
      |					})
      |</script>
      |
    """.stripMargin

  val phylotreeCss =
    """
      |<style>
      |.tree-selection-brush .extent {
      |    fill-opacity: .05;
      |    stroke: #fff;
      |    shape-rendering: crispEdges;
      |}
      |
      |.tree-scale-bar text {
      |  font: sans-serif;
      |}
      |
      |.tree-scale-bar line,
      |.tree-scale-bar path {
      |  fill: none;
      |  stroke: #000;
      |  shape-rendering: crispEdges;
      |}
      |
      |.node circle, .node ellipse, .node rect {
      |fill: steelblue;
      |stroke: black;
      |stroke-width: 0.5px;
      |}
      |
      |.internal-node circle, .internal-node ellipse, .internal-node rect{
      |fill: #CCC;
      |stroke: black;
      |stroke-width: 0.5px;
      |}
      |
      |.node {
      |font: 10px sans-serif;
      |}
      |
      |.node-selected {
      |fill: #f00 !important;
      |}
      |
      |.node-collapsed circle, .node-collapsed ellipse, .node-collapsed rect{
      |fill: black !important;
      |}
      |
      |.node-tagged {
      |fill: #00f;
      |}
      |
      |.branch {
      |fill: none;
      |stroke: #999;
      |stroke-width: 2px;
      |}
      |
      |.clade {
      |fill: #1f77b4;
      |stroke: #444;
      |stroke-width: 2px;
      |opacity: 0.5;
      |}
      |
      |.branch-selected {
      |stroke: #f00 !important;
      |stroke-width: 3px;
      |}
      |
      |.branch-tagged {
      |stroke: #00f;
      |stroke-dasharray: 10,5;
      |stroke-width: 2px;
      |}
      |
      |.branch-tracer {
      |stroke: #bbb;
      |stroke-dasharray: 3,4;
      |stroke-width: 1px;
      |}
      |
      |
      |.branch-multiple {
      |stroke-dasharray: 5, 5, 1, 5;
      |stroke-width: 3px;
      |}
      |
      |.branch:hover {
      |stroke-width: 10px;
      |}
      |
      |.internal-node circle:hover, .internal-node ellipse:hover, .internal-node rect:hover {
      |fill: black;
      |stroke: #CCC;
      |}
      |
      |.tree-widget {
      |}
      |</style>
    """.stripMargin

  def createDirectoryWhenNoExist(file: File): Unit = {
    if (!file.exists && !file.isDirectory) file.mkdir
  }

  def xlsx2Txt(xlsxFile: File, txtFile: File) = {

    val is = new FileInputStream(xlsxFile.getAbsolutePath)
    val xssfWorkbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)
    val xssfSheet = xssfWorkbook.getSheetAt(0)
    val lines = ArrayBuffer[String]()
    for (xssfRow <- xssfSheet.asScala) {
      val columns = ArrayBuffer[String]()
      for (j <- 0 until xssfRow.getLastCellNum) {
        val cell = xssfRow.getCell(j)
        var value = ""
        if (cell != null) {
          cell.getCellType match {
            case CellType.STRING =>
              value = cell.getStringCellValue
            case CellType.NUMERIC =>
              if (DateUtil.isCellDateFormatted(cell)) {
                val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
                value = dateFormat.format(cell.getDateCellValue)
              } else {
                value = cell.getStringCellValue
              }
            case CellType.BLANK =>
              value = ""
            case _ =>
              value = ""
          }
        }

        columns += value.replaceAll("\n", " ")
      }
      val line = columns.mkString("\t")
      lines += line
    }
    lines.notEmptyLines.toFile(txtFile)
    xssfWorkbook.close()
  }

  def xlsx2Lines(xlsxFile: File): ArrayBuffer[String] = {
    xlsx2Lines(xlsxFile, 0)
  }

  def scale100(x: BigDecimal, maxScale: Int) = {
    val scale = x.scale - 2
    val finalScale = if (scale >= maxScale) maxScale else if (scale >= 0) scale else 0
    (x * 100).setScale(finalScale, RoundingMode.HALF_UP)
  }

  def xlsx2Lines(xlsxFile: File, sheetIndex: Int): ArrayBuffer[String] = {
    val is = new FileInputStream(xlsxFile.getAbsolutePath)
    val xssfWorkbook = new XSSFWorkbook(is)
    val xssfSheet = xssfWorkbook.getSheetAt(sheetIndex)
    val lines = ArrayBuffer[String]()
    for (i <- 0 to xssfSheet.getLastRowNum) {
      val xssfRow = xssfSheet.getRow(i)
      val columns = ArrayBuffer[String]()
      for (j <- 0 until xssfRow.getLastCellNum) {
        val cell = xssfRow.getCell(j)
        var value = ""
        if (cell != null) {
          cell.getCellType match {
            case CellType.STRING =>
              value = cell.getStringCellValue
            case CellType.NUMERIC =>
              if (DateUtil.isCellDateFormatted(cell)) {
                val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
                value = dateFormat.format(cell.getDateCellValue)
              } else {
                val doubleValue = cell.getNumericCellValue
                value = if (doubleValue == doubleValue.toInt) {
                  doubleValue.toInt.toString
                } else doubleValue.toString
              }
            case CellType.BLANK =>
              value = ""
            case _ =>
              value = ""
          }
        }

        columns += value.replaceAll("\n", " ")
      }
      val line = columns.mkString("\t")
      lines += line
    }
    xssfWorkbook.close()
    lines.filter(StringUtils.isNotBlank(_))
  }

  def lines2columns(lines: mutable.Buffer[String]) = {
    lines.map(_.split("\t").toBuffer)
  }

  def containsChinese(str: String) = {
    val regex = "[\\u4E00-\\u9FBF]+".r
    regex.findFirstIn(str).isDefined
  }


  def getSuffix(file: File) = {
    val fileName = file.getName
    val index = fileName.lastIndexOf(".")
    fileName.substring(0, index)
  }

  def getSuffix(path: String) = {
    val index = path.lastIndexOf(".")
    path.substring(index + 1)
  }

  def getLocalMac = {
    val ia = InetAddress.getLocalHost
    val mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress
    val sb = new StringBuilder
    for (i <- 0 until mac.length) {
      if (i != 0) sb.append("-")
      val temp = mac(i) & 0xff
      val str = Integer.toHexString(temp)
      if (str.length == 1) sb.append("0" + str) else sb.append(str)
    }
    sb.toString().toUpperCase
  }

  def sendMessage(phone: String) = {
    System.setProperty("sun.net.client.defaultConnectTimeout", "10000")
    System.setProperty("sun.net.client.defaultReadTimeout", "10000")
    val product = "Dysmsapi"
    val domain = "dysmsapi.aliyuncs.com"
    val accessKeyId = "LTAIP0hniUIcRMyS"
    val accessKeySecret = "CGqafePNpVt9IaRYph3QpJp2LvHXim"
    val profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret)
    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain)
    val acsClient = new DefaultAcsClient(profile)
    val request = new SendSmsRequest
    request.setMethod(MethodType.POST)
    request.setPhoneNumbers(phone)
    request.setSignName("阿里云短信测试专用")
    request.setTemplateCode("SMS_139960130")
    val code = productValidCode
    val json = Json.obj("code" -> code)
    val jsonString = Json.stringify(json)
    request.setTemplateParam(jsonString)
    val sendSmsResponse = acsClient.getAcsResponse(request)
    val responseCode = sendSmsResponse.getCode
    if (sendSmsResponse.getCode != null && sendSmsResponse.getCode.equals("OK")) {
      (true, code, responseCode)
    } else {
      println(sendSmsResponse.getCode)
      (false, code, responseCode)
    }
  }

  def getIp(implicit request: RequestHeader) = request.remoteAddress

  def result2Future(result: Result) = {
    Future.successful(result)
  }

  def productValidCode = {
    "000000".map { i =>
      (Random.nextInt(10) + '0').toChar
    }
  }

  def callScript(tmpDir: File, shBuffer: ArrayBuffer[String]) = {
    val execCommand = new ExecCommand
    val runFile = if (Utils.isWindows) {
      new File(tmpDir, "run.bat")
    } else {
      new File(tmpDir, "run.sh")
    }
    shBuffer.toFile(runFile)
    val shCommand = runFile.getAbsolutePath
    if (Utils.isWindows) {
      execCommand.exec(shCommand, tmpDir)
    } else {
      val useCommand = "chmod +x " + runFile.getAbsolutePath
      val dos2Unix = "dos2unix " + runFile.getAbsolutePath
      execCommand.exec(dos2Unix, useCommand, shCommand, tmpDir)
    }
    execCommand
  }


  def deleteDirectory(direcotry: File) = {
    try {
      FileUtils.deleteDirectory(direcotry)
    } catch {
      case _ =>
    }
  }

  def deleteFile(file: File) = {
    if (file.exists()) file.delete()
  }

  def getTime(startTime: Long) = {
    val endTime = System.currentTimeMillis()
    (endTime - startTime) / 1000.0
  }

  def logTime(startTime: Long) = {
    val time = getTime(startTime)
    println(time)
  }

  def isDoubleP(value: String, p: Double => Boolean): Boolean = {
    try {
      val dbValue = value.toDouble
      p(dbValue)
    } catch {
      case _: Exception =>
        false
    }
  }

  def getGroupNum(content: String) = {
    content.split(";").size
  }

  def getMap(content: String) = {
    val map = mutable.LinkedHashMap[String, mutable.Buffer[String]]()
    content.split(";").foreach { x =>
      val columns = x.split(":")
      map += (columns(0) -> columns(1).split(",").toBuffer)
    }
    map
  }

  def getGroupNames(content: String) = {
    val map = getMap(content)
    map.keys.toBuffer

  }

  def lfJoin(seq: Seq[String]) = {
    seq.mkString("\n")
  }

  def execFuture[T](f: Future[T]): T = {
    Await.result(f, Duration.Inf)
  }

  def getValue[T](kind: T, noneMessage: String = ""): String = {
    kind match {
      case x if x.isInstanceOf[DateTime] => val time = x.asInstanceOf[DateTime]
        time.toString("yyyy-MM-dd HH:mm:ss")
      case x if x.isInstanceOf[Option[T]] => val option = x.asInstanceOf[Option[T]]
        if (option.isDefined) getValue(option.get, noneMessage) else noneMessage
      case _ => kind.toString
    }
  }

  def getArrayByTs[T](x: Seq[T]) = {
    x.map { y =>
      y.getClass.getDeclaredFields.toBuffer.map { x: Field =>
        x.setAccessible(true)
        val kind = x.get(y)
        val value = getValue(kind)
        (x.getName, value)
      }.init.toMap
    }
  }

  def getMapByT[T](t: T) = {
    t.getClass.getDeclaredFields.toBuffer.map { x: Field =>
      x.setAccessible(true)
      val kind = x.get(t)
      val value = getValue(kind)
      (x.getName, value)
    }.init.toMap
  }

  def getMapByT[T](t: T, mapField: String) = {
    t.getClass.getDeclaredFields.toBuffer.flatMap { x: Field =>
      x.setAccessible(true)
      val kind = x.get(t)
      val filedName = x.getName
      if (filedName == mapField) {
        Utils.str2Map(kind.toString)
      } else {
        val value = getValue(kind)
        Map(filedName -> value)
      }
    }.init.toMap
  }

  def getMapByT[T](t: T, excludeFields: Seq[String]) = {
    t.getClass.getDeclaredFields.toBuffer.filter { x: Field =>
      val filedName = x.getName
      !excludeFields.contains(filedName)
    }.map { x: Field =>
      x.setAccessible(true)
      val kind = x.get(t)
      val value = getValue(kind)
      (x.getName, value)
    }.init.toMap

  }

  def getArrayByTs[T](x: Seq[T], mapField: String) = {
    x.map { y =>
      getMapByT(y, mapField)
    }
  }

  def getArrayByTs[T](x: Seq[T], excludeFields: Seq[String]) = {
    x.map { y =>
      getMapByT(y, excludeFields)
    }
  }

  def getMapByOpT[T](tOp: Option[T]) = {
    tOp match {
      case Some(t) =>
        t.getClass.getDeclaredFields.toBuffer.map { x: Field =>
          x.setAccessible(true)
          val kind = x.get(t)
          val value = getValue(kind)
          (x.getName, value)
        }.init.toMap
      case None => Map[String, String]()
    }

  }

  def getJsonByT[T](y: T) = {
    val map = getMapByT(y)
    Json.toJson(map)
  }

  def getJsonByTs[T](x: Seq[T]) = {
    val array = getArrayByTs(x)
    Json.toJson(array)
  }

  //
  //  def log2(x: Double) = log10(x) / log10(2.0)
  //
  //  def getStdErr(values: Array[Double]) = {
  //    val standardDeviation = new StandardDeviation
  //    val stderr = standardDeviation.evaluate(values) / Math.sqrt(values.length)
  //    stderr
  //  }

  def dealGeneIds(geneId: String) = {
    geneId.split("\n").map(_.trim).distinct.toBuffer
  }

  def getDataJson(file: File) = {
    val lines = file.lines
    val sampleNames = lines.head.split("\t").drop(1)
    val array = lines.drop(1).map { line =>
      val columns = line.split("\t")
      val map = mutable.Map[String, String]()
      map += ("geneId" -> columns(0))
      sampleNames.zip(columns.drop(1)).foreach { case (sampleName, data) =>
        map += (sampleName -> data)
      }
      map
    }
    Json.obj("array" -> array, "sampleNames" -> sampleNames)
  }

  //  def pdf2png(tmpDir: File, fileName: String) = {
  //    val pdfFile = new File(tmpDir, fileName)
  //    val outFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".png"
  //    val outFile = new File(tmpDir, outFileName)
  //    val document = PDDocument.load(pdfFile)
  //    val renderer = new PDFRenderer(document)
  //    ImageIO.write(renderer.renderImage(0, 3), "png", outFile)
  //    document.close()
  //  }
  //
  def getInfoByFile(file: File) = {
    val lines = file.lines
    val columnNames = lines.head.split("\t").toBuffer
    val array = lines.drop(1).map { line =>
      val columns = line.split("\t")
      columnNames.zip(columns).toMap
    }
    (columnNames, array)
  }

  def detectCoding(file: File) = {
    try {
      EncodingDetect.getJavaEncode(file.getAbsolutePath)
    } catch {
      case e: Exception => "UTF-8"
    }

  }

  def getInfoByFile(file: File, to: Int) = {
    val lines = file.lines
    val n = to + 1
    val columnNames = lines.head.split("\t").toBuffer.take(n)
    val array = lines.drop(1).map { line =>
      val columns = line.split("\t").take(n)
      columnNames.zip(columns).toMap
    }
    (columnNames, array)
  }

  def file2Str(file: File) = {
    FileUtils.readFileToString(file)
  }

  def str2Map(str: String) = {
    Json.parse(str).asOpt[Map[String, String]].getOrElse(Map[String, String]())
  }

  def map2Str(map: Map[String, String]) = {
    Json.toJson(map).toString()
  }

  def str2Date(str: String) = {
    val format = new SimpleDateFormat(Shared.pattern)
    val d = format.parse(str)
    new Date(d.getTime)
  }

  def str2Json(str: String) = {
    Json.parse(str)
  }

  def checkFile(file: File): (Boolean, String) = {
    val buffer = file.lines
    val headers = buffer.head.split("\t")
    var error = ""
    if (headers.size < 2) {
      error = "错误：文件列数小于2！"
      return (false, error)
    }
    val headersNoHead = headers.drop(1)
    val repeatElement = headersNoHead.diff(headersNoHead.distinct).distinct.headOption
    repeatElement match {
      case Some(x) => val nums = headers.zipWithIndex.filter(_._1 == x).map(_._2 + 1).mkString("(", "、", ")")
        error = "错误：样品名" + x + "在第" + nums + "列重复出现！"
        return (false, error)
      case None =>
    }

    val ids = buffer.drop(1).map(_.split("\t")(0))
    val repeatid = ids.diff(ids.distinct).distinct.headOption
    repeatid match {
      case Some(x) => val nums = ids.zipWithIndex.filter(_._1 == x).map(_._2 + 2).mkString("(", "、", ")")
        error = "错误：第一列:" + x + "在第" + nums + "行重复出现！"
        return (false, error)
      case None =>
    }

    val headerSize = headers.size
    for (i <- 1 until buffer.size) {
      val columns = buffer(i).split("\t")
      if (columns.size != headerSize) {
        error = "错误：数据文件第" + (i + 1) + "行列数不对！"
        return (false, error)
      }

    }

    for (i <- 1 until buffer.size) {
      val columns = buffer(i).split("\t")
      for (j <- 1 until columns.size) {
        val value = columns(j)
        if (!isDouble(value)) {
          error = "错误：数据文件第" + (i + 1) + "行第" + (j + 1) + "列不为数字！"
          return (false, error)
        }
      }
    }
    (true, error)
  }

  def isDouble(value: String): Boolean = {
    try {
      value.toDouble
    } catch {
      case _: Exception =>
        return false
    }
    true
  }

  def isInterger(value: String): Boolean = {
    try {
      value.toDouble
    } catch {
      case _: Exception =>
        return false
    }
    true
  }

  def getBase64Str(imageFile: File): String = {
    val inputStream = new FileInputStream(imageFile)
    val bytes = IOUtils.toByteArray(inputStream)
    val bytes64 = Base64.encodeBase64(bytes)
    inputStream.close()
    new String(bytes64)
  }

}
