package test

import java.io.{BufferedInputStream, File, FileInputStream, InputStreamReader}

import com.ibm.icu.text.CharsetDetector
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import tool.Tool
import utils.{EncodingDetect, Utils}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import utils.Implicits._

/**
  * Created by yz on 2018/7/27
  */
object Test {

  val snpCoreset = ArrayBuffer("M217", "B473", "P47", "M201", "M407", "BY728", "M15", "F948", "Z1300", "M20", "M304", "M55", "F845",
    "M96", "M130", "F1396", "M174", "M77", "M48", "M170", "F1918", "F1756", "M9", "M89", "M231", "F14",
    "L901", "F1067", "P295", "M184")
  val snpN = ArrayBuffer("F3361", "M128", "F1833", "L1420", "M1854", "M46", "SK1512", "PH68", "CTS1714", "F4065", "F4250",
    "SK1507", "B187", "M231", "F4156", "F846", "F2584", "B197", "F839", "M1982", "SK2246", "F2930", "Z34965",
    "M2120", "B182", "PH432", "F4205", "M178", "CTS4714")
  val snpP = ArrayBuffer("F1857", "F4529", "F4530", "F4531", "PH1003", "F1827", "M120", "M3", "F835", "L53", "F844", "M242",
    "YP771", "M420", "Y13199", "L54", "L330", "M207", "M25", "SK1927", "YP1102", "L275", "L232", "SK1925",
    "L278", "M269", "Y558", "M346", "M512", "F903")
  val snp01 = ArrayBuffer("M119", "F157", "CTS701", "F619", "K644", "F153", "Z23482", "SK1555", "SK1527", "M101", "P203.1",
    "F533", "F18460", "CTS5576", "F656", "CTS409", "SK1533", "SK1567", "M50", "CTS52", "F6226", "F65",
    "F4253", "F794", "F5498", "F446", "F3288", "Z23271", "F78", "Z38607")
  val snp02 = ArrayBuffer("M268", "F923", "F2868", "F1252", "M88", "Z23743", "PK4", "F2061", "SK1630", "F840", "F5503",
    "F1204", "F2758", "Y14005", "L682", "M1283", "M1368", "F1462", "M1410", "SK1636", "F809", "F2924",
    "F838", "F2489", "CTS713", "F2124", "F3079", "F993", "M95", "M176")
  val snpGamma = ArrayBuffer("F930", "G6795544A", "SK1691", "SK1676", "F197", "F196", "F2680", "F1422", "M122", "F2527",
    "F11", "F309", "F17", "F793", "F133", "F718", "F1495", "F856", "F377", "M324", "F18", "F38", "F632",
    "F3232", "SK1675", "F449", "P201", "M188", "N6", "CTS10573")
  val snpAlpha = ArrayBuffer("F141", "F316", "F14441", "B456", "F1442", "F375", "F438", "CTS7634", "CTS5063", "CTS4658",
    "G7690556T", "F14196", "F14245", "F14523", "A9462", "F2137", "F402", "F14274", "F1123", "F148",
    "M1543", "F8", "F317", "F813", "CTS9713", "F14682", "F7479", "F1754", "CTS1154", "F5970")
  val snpBeta = ArrayBuffer("F444", "F79", "F46", "F48", "F55", "F152", "F563", "F209", "F2173", "F14521", "CTS3776",
    "CTS3763", "KM3028", "F5535", "F4249", "F3607", "F823", "CTS335", "L1360", "KM3031", "CTS53",
    "F1326", "F2903", "CTS4266", "CTS1933", "CTS1346", "F14214", "CTS2056")

  var snpMap = Map[String, mutable.Buffer[String]]()

  val parent = new File("G:\\ychr\\test")

  def main(args: Array[String]): Unit = {

//    val rs = ArrayBuffer(15, 27, 38, 46, 89, 75)
//    val startTime = System.currentTimeMillis()
//    val file = new File(parent, "test.txt")
//    val encoding = EncodingDetect.getJavaEncode(file.getAbsolutePath)
//    println(encoding)
//    val bis=new BufferedInputStream(new FileInputStream(file))
//    val cd=new CharsetDetector
//    cd.setText(bis)
//    val encoding2=cd.detect().getName
//    println(encoding2)
//    val lines=file.lines(encoding)
//    println(lines)
//    val lines1=file.lines(encoding2)
//    println(lines1)

    //    val file=new File(parent,"中国姓氏大全.sql")
    //    val lines=file.lines
    //    println(lines.map("\""+_+"\""))

    //    val startTime = System.currentTimeMillis()
    //    val file=new File(parent,"SNP-gama.dat")
    //    val outFile=new File(parent,"snp.txt")
    //    Tool.dat2txt(file,outFile)


    //    val file = new File(parent, "Y database_3(1).txt")
    //    val lines=Utils.file2Lines(dataFile)
    //    val lines = FileUtils.readLines(file,"GBK").asScala
    //    Utils.lines2File(new File(parent,"data.txt"),lines)

    {
      //            productDataTxtFile
      //            productHeadTxtFile
      //            productDataWithIndelFile
      //            productPanelTxtFile
      //      productDataPanelFile
      //      productInfoFile

    }

    //    {
    //      val parent = new File("E:\\ychr\\test")
    //      val file = new File(parent, "姓氏民族地理.xlsx")
    //      val lines = Utils.xlsx2Lines(file)
    //      val nameMap = Utils.lines2columns(lines).map { columns =>
    //        (columns(0), columns(1))
    //      }.toMap
    //      val sheet2Lines = Utils.xlsx2Lines(file, 1)
    //      val naMap = Utils.lines2columns(sheet2Lines).map { columns =>
    //        (columns(0), (columns(1), columns(2)))
    //      }.toMap
    //      val filterNameMap=nameMap.filter{case(key,value)=>
    //        !naMap.contains(key)
    //      }

    //    }


    //    snpMap = productSiteName
    //    addLineNumColumn
    //    filterNull
    //    confirmYSnp
    //    doKind
    //    getInfo
    //    addHeader

    //    calculateDistance
    //    distanceMerge
    //    matrixMerge


    //    println(Utils.getTime(startTime))
  }

  def productInfoFile = {
    val dataPanelFile = new File(parent, "data_panel.txt")
    val infoFile = new File(parent, "data_info.txt")
    val lines = dataPanelFile.lines
    val dataFile = new File(parent, "data.txt")
    val header = FileUtils.readLines(dataFile).asScala.head.split("\t").slice(9, 26).map { x =>
      x match {
        case "H4" => "YGATAH4"
        case "DYS389b" => "DYS389II"
        case _ => x
      }
    }.mkString("\t")
    val newLines = ArrayBuffer(s"kind\tindex\t${header}") ++= lines.filter { line =>
      val columns = line.split("\t")
      val panel = columns(0)
      panel != "-"
    }.map { line =>
      val columns = line.split("\t")
      val panel = columns(0)
      val myPanel = panel match {
        case "Oα" | "Oα||Oβ" => "Oα"
        case "Oγ" | "Oγ||Oα||Oβ" => "Oγ"
        case "O1b" => "O1b"
        case "O1a" => "O1a"
        case "P" => "P"
        case "N" => "N"
        case "Oβ" => "Oβ"
      }
      (ArrayBuffer(myPanel) ++= columns.drop(1)).mkString("\t")
    }.map { line =>
      val columns = line.split("\t").toBuffer
      val newColumns = columns.take(2) ++= columns.slice(11, 28)
      newColumns.mkString("\t")
    }
    newLines.toFile(infoFile)

  }

  def productDataPanelFile = {
    val dataFile = new File(parent, "data_index.txt")
    val panelFile = new File(parent, "panel.txt")
    val dataPanelFile = new File(parent, "data_panel.txt")
    val dataLines = dataFile.lines
    val panelLines = panelFile.lines.drop(1)
    val map = panelLines.map { line =>
      val columns = line.split("\t")
      (columns(0), columns(1))
    }.toMap
    val newLines = dataLines.filter { line =>
      val columns = line.split("\t")
      val id = columns(0)
      map.contains(id)
    }.map { line =>
      val columns = line.split("\t")
      val id = columns(0)
      val panel = map(id)
      s"${panel}\t${line}"
    }
    newLines.toFile(dataPanelFile)
  }

  def productDataWithIndelFile = {
    val dataFile = new File(parent, "data.txt")
    val headFile = new File(parent, "head.txt")
    val dataIndexFile = new File(parent, "data_index.txt")
    val dataLines = dataFile.lines.drop(4)
    val headLines = headFile.lines.drop(1)
    val newLines = headLines.zip(dataLines).map { case (headLine, dataLine) =>
      s"${headLine.split("\t")(0)}\t${dataLine}"
    }
    newLines.toFile(dataIndexFile)
  }

  def productDataTxtFile = {
    val xlsxFile = new File(parent, "Y database_3(1).xlsx")
    val dataFile = new File(parent, "data.txt")
    Utils.xlsx2Txt(xlsxFile, dataFile)
  }

  def productPanelTxtFile = {
    val xlsxFile = new File(parent, "PANEL标注.xlsx")
    val dataFile = new File(parent, "panel.txt")
    Utils.xlsx2Txt(xlsxFile, dataFile)
  }

  def productHeadTxtFile = {
    val xlsxFile = new File(parent, "head.xlsx")
    val dataFile = new File(parent, "head.txt")
    Utils.xlsx2Txt(xlsxFile, dataFile)
  }

  def getPrefix(str1: String, str2: String) = {
    str1.zip(str2).takeWhile { case (x, y) => x == y }.unzip._1.mkString
  }

  def getInfo = {
    val file = new File(parent, s"Y database_3_kind.txt")
    val lines = file2Lines(file)
    val newLines = lines.map { line =>
      val columns = line.split("\t").toBuffer
      (columns.take(2) ++= columns.slice(11, 30)).mkString("\t")
    }
    newLines.toFile(new File(parent, "info_1.txt"))
  }

  def file2Lines(file: File) = {
    FileUtils.readLines(file).asScala
  }

  def lines2Columns(lines: mutable.Buffer[String]) = {
    lines.map(x => x.split("\t"))
  }

  def uniqSnp = {
    val file = new File("E:\\ychr\\Y database_3_filter_null.txt")
    val lines = FileUtils.readLines(file).asScala
    val map = mutable.LinkedHashMap[String, String]()
    lines.foreach { line =>
      val columns = line.split("\t")
      map += (columns(9) -> line)
    }
    val newLines = map.values.toBuffer
    println(newLines.size)
    newLines.take(20).foreach(println(_))
    newLines.toFile(new File("E:\\ychr\\Y database_3_uniq_snp.txt"))
  }

  def uniqData = {
    val file = new File("E:\\ychr\\Y database_3_index.txt")
    val lines = FileUtils.readLines(file).asScala
    val map = mutable.LinkedHashMap[String, String]()
    lines.foreach { line =>
      val columns = line.split("\t")
      val key = columns.slice(10, 30).mkString("\t")
      map += (key -> line)
    }
    val newLines = map.values.toBuffer
    println(newLines.size)
    map.take(20).foreach(println(_))
    newLines.toFile(new File("E:\\ychr\\Y database_3_uniq_data.txt"))
  }

  def addHeader = {
    val ysnpFile = new File(parent, "Y database_3_y_snp.txt")
    val header = FileUtils.readLines(ysnpFile).asScala.head.split("\t").slice(10, 30).map { x =>
      x match {
        case "H4" => "YGATAH4"
        case "DYS389b" => "DYS389II"
        case _ => x
      }
    }.mkString("\t")
    val file = new File(parent, "info_1.txt")
    val lines = FileUtils.readLines(file).asScala
    val newLines = ArrayBuffer(s"kind\tindex\t${header}") ++= lines
    newLines.toFile(new File(parent, "info_2.txt"))
  }

  //  def addKind = {
  //    val file = new File(parent, "output.txt")
  //    val kindFile = new File(parent, s"Y database_3_distance_merge.txt")
  //    val kindMap = FileUtils.readLines(kindFile).asScala.map { line =>
  //      val columns = line.split("\t")
  //      (columns(1), columns(0))
  //    }.toMap
  //    val newLines = FileUtils.readLines(file).asScala.zipWithIndex.map { case (line, i) =>
  //      val columns = line.split("\t")
  //      if (i == 0) {
  //        s"kind\tindex\tpc1\tpc2"
  //      } else {
  //        Array(kindMap(columns(0)), columns(0), columns(1), columns(2)).mkString("\t")
  //      }
  //    }
  //    FileUtils.writeLines(new File(parent, "pca.txt"), newLines.asJava)
  //
  //
  //  }

  def distanceMerge = {
    val kinds = snpMap.keys.toBuffer
    val newLines = kinds.flatMap { kind =>
      val file = new File(parent, s"Y database_3_${kind}_distance.txt")
      val lines = FileUtils.readLines(file).asScala
      lines.map { line =>
        val columns = line.split("\t")
        s"${kind}\t${columns(0)}\t${columns(1)}\t${columns.slice(11, 31).mkString("\t")}"
      }
    }
    val outFile = new File(parent, s"Y database_3_distance_merge.txt")
    newLines.toFile(outFile)
  }

  def matrixMerge = {
    val map = mutable.LinkedHashMap[String, String]()
    (1 to 4).map { i =>
      s"matrix_${i}.txt"
    }.foreach { fileName =>
      val file = new File(parent, fileName)
      FileUtils.readLines(file).asScala.foreach { line =>
        map += (line.split("\t")(0) -> line)
      }
    }
    val file = new File(parent, s"Y database_3_distance_merge.txt")
    val lines = FileUtils.readLines(file).asScala
    val indexs = lines.map(_.split("\t")(1))
    val header = s"\t" + indexs.mkString("\t")
    val newLines = ArrayBuffer(header) ++= indexs.map { index =>
      map(index)
    }
    newLines.toFile(new File(parent, "matrix_merge.txt"))
  }

  def distanceMatrix = {
    val file = new File(parent, s"Y database_3_distance_merge.txt")
    val lines = FileUtils.readLines(file).asScala
    val map = mutable.LinkedHashMap[String, Array[String]]()
    lines.foreach { line =>
      val columns = line.split("\t")
      val datas = columns.slice(3, 22)
      val index = columns(1)
      map += (index -> datas)
    }
    val indexs = map.keys.toBuffer
    val header = ArrayBuffer("") ++= indexs
    val weights = ArrayBuffer(4, 3, 2, 4, 3, 20, 8, 7, 27, 2, 5, 2, 1, 2, 3, 1, 0.3, 9, 20)
    val newLines = ArrayBuffer(header)
    val threadNum = 4
    val groupMap = indexs.zipWithIndex.map { case (v, i) =>
      val j = (i % threadNum) + 1
      (j, v)
    }.groupBy(_._1).mapValues(_.map(_._2))

    def getF(i: Int) = {
      val values = groupMap(i)
      val f1 = Future {
        val newLines = values.zipWithIndex.map { case (index, i) =>
          if (i % 500 == 0) println(index)
          s"${index}\t" + indexs.map { otherIndex =>
            val data = map(index)
            val otherData = map(otherIndex)
            val tmpDistances = data.zipWithIndex.map { case (v, i) =>
              (data(i), otherData(i), weights(i))
            }.withFilter { case (x, y, z) =>
              StringUtils.isNotBlank(x) && StringUtils.isNotBlank(y)
            }.withFilter { case (x, y, z) =>
              Utils.isDouble(x) && Utils.isDouble(y)
            }.map { case (x, y, z) =>
              math.abs(x.toDouble - y.toDouble) * z
            }
            (tmpDistances.sum / tmpDistances.size).toString
          }.mkString("\t")
        }
        FileUtils.writeLines(new File(parent, s"matrix_${i}.txt"), newLines.asJava)
      }
      f1
    }

    var f = getF(1).zip(getF(2)).zip(getF(3)).zip(getF(4))
    Utils.execFuture(f)
  }

  def calculateDistance = {
    val kinds = snpMap.keys.toBuffer
    kinds.foreach { kind =>
      println(kind)
      val startTime = System.currentTimeMillis()
      val file = new File(parent, s"Y database_3_${kind}_merge.txt")
      val lines = FileUtils.readLines(file).asScala.map(_.split("\t").map(removeQuote(_)).toBuffer)
      val newLines = lines.map { columns =>
        val ortherLines = lines - columns
        val distances = ortherLines.map { otherColumns =>
          val datas = columns.slice(10, 29)
          val ortherDatas = otherColumns.slice(10, 29)
          val weights = ArrayBuffer(4, 3, 2, 4, 3, 20, 8, 7, 27, 2, 5, 2, 1, 2, 3, 1, 0.3, 9, 20)
          val tmpDistances = datas.indices.map { i =>
            (datas(i), ortherDatas(i), weights(i))
          }.withFilter { case (x, y, z) =>
            StringUtils.isNotBlank(x) && StringUtils.isNotBlank(y)
          }.withFilter { case (x, y, z) =>
            Utils.isDouble(x) && Utils.isDouble(y)
          }.map { case (x, y, z) =>
            math.abs(x.toDouble - y.toDouble) * z
          }
          tmpDistances.sum / tmpDistances.size
        }
        val avgDistance = distances.sum / distances.size
        (columns.take(1) += avgDistance.toString ++= columns.drop(1)).mkString("\t")
      }.sortBy(_.split("\t")(1).toDouble)
      println(Utils.getTime(startTime))
      FileUtils.writeLines(new File(parent, s"Y database_3_${kind}_distance.txt"), newLines.asJava)
    }
  }

  def confirmYSnp = {
    val newLines = FileUtils.readLines(new File(parent, "Y database_3_filter_null.txt")).asScala
    val ySnpLines = newLines.filter { line =>
      val columns = line.split("\t")
      StringUtils.isNotBlank(columns(9))
    }
    println(ySnpLines.size)
    FileUtils.writeLines(new File(parent, "Y database_3_y_snp.txt"), ySnpLines.asJava)
  }

  def filterNull = {
    val file = new File(parent, "Y database_3_index.txt")
    val lines = FileUtils.readLines(file).asScala
    println(lines.size)
    val newLines = lines.filter { line =>
      val columns = line.split("\t")
      columns.slice(10, 30).filter(x => StringUtils.isBlank(x)).size <= 4
    }
    println(newLines.size)
    FileUtils.writeLines(new File(parent, "Y database_3_filter_null.txt"), newLines.asJava)
  }

  def addLineNumColumn = {
    val file = new File(parent, "Y database_3.txt")
    val lines = FileUtils.readLines(file, "GBK").asScala
    val newLines = lines.zipWithIndex.map { case (line, i) =>
      s"${i + 1}\t${line}"
    }
    FileUtils.writeLines(new File(parent, "Y database_3_index.txt"), newLines.asJava)
  }

  def removeQuote(value: String) = {
    value.replaceAll("^\"", "").replaceAll("\"$", "")
  }


}
