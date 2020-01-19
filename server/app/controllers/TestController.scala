//package controllers
//
//import java.io.File
//import java.nio.file.Files
//
//import javax.inject.Inject
//import org.apache.commons.io.FileUtils
//import org.apache.commons.lang3.StringUtils
//import org.apache.hadoop.conf.Configuration
//import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
//import org.apache.spark.ml.feature.{PCA, RFormula}
//import org.apache.spark.ml.linalg.DenseVector
//import org.apache.spark.mllib.linalg
//import org.apache.spark.mllib.linalg.Vectors
//import org.apache.spark.mllib.linalg.distributed.RowMatrix
//import org.apache.spark.mllib.regression.LabeledPoint
//import org.apache.spark.rdd.RDD
//import org.apache.spark.sql.{DataFrame, Row}
//import play.api.mvc.{AbstractController, ControllerComponents}
//import spark.Init
//import utils.{Util, Utils}
//
//import scala.collection.JavaConverters._
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.types.DoubleType
//
//import scala.collection.mutable
//import scala.collection.mutable.ArrayBuffer
//
///**
//  * Created by yz on 2018/11/16
//  */
//class TestController @Inject()(cc: ControllerComponents, init: Init, util: Util,tool:Tool) extends AbstractController(cc) {
//  val snpCoreset = ArrayBuffer("M217", "B473", "P47", "M201", "M407", "BY728", "M15", "F948", "Z1300", "M20", "M304", "M55", "F845",
//    "M96", "M130", "F1396", "M174", "M77", "M48", "M170", "F1918", "F1756", "M9", "M89", "F14",
//    "L901", "F1067", "P295", "M184")
//  val snpN = ArrayBuffer("F3361", "M128", "F1833", "L1420", "M1854", "M46", "SK1512", "PH68", "CTS1714", "F4065", "F4250",
//    "SK1507", "B187", "F4156", "F846", "F2584", "B197", "F839", "M1982", "SK2246", "F2930", "Z34965",
//    "M2120", "B182", "PH432", "F4205", "M178", "CTS4714", "CTS1303")
//  val snpP = ArrayBuffer("F1857", "F4529", "F4530", "F4531", "PH1003", "F1827", "M120", "M3", "F835", "L53", "F844", "M242",
//    "YP771", "M420", "Y13199", "L54", "L330", "M207", "M25", "SK1927", "YP1102", "L275", "L232", "SK1925",
//    "L278", "M269", "Y558", "M346", "M512", "F903")
//  val snp01 = ArrayBuffer("M119", "F157", "CTS701", "F619", "K644", "F153", "Z23482", "SK1555", "SK1527", "M101", "P203.1",
//    "F533", "F18460", "CTS5576", "F656", "CTS409", "SK1533", "SK1567", "M50", "CTS52", "F6226", "F65",
//    "F4253", "F794", "F5498", "F446", "F3288", "Z23271", "F78", "Z38607")
//  val snp02 = ArrayBuffer("M268", "F923", "F2868", "F1252", "M88", "Z23743", "PK4", "F2061", "SK1630", "F840", "F5503",
//    "F1204", "F2758", "Y14005", "L682", "M1283", "M1368", "F1462", "M1410", "SK1636", "F809", "F2924",
//    "F838", "F2489", "CTS713", "F2124", "F3079", "F993", "M95", "M176")
//  val snpGamma = ArrayBuffer("F930", "G6795544A", "SK1691", "SK1676", "F197", "F196", "F2680", "F1422", "M122", "F2527",
//    "F11", "F309", "F17", "F793", "F133", "F718", "F1495", "F856", "F377", "M324", "F18", "F38", "F632",
//    "F3232", "SK1675", "F449", "P201", "M188", "N6", "CTS10573")
//  val snpAlpha = ArrayBuffer("F141", "F316", "F14441", "B456", "F1442", "F375", "F438", "CTS7634", "CTS5063", "CTS4658",
//    "G7690556T", "F14196", "F14245", "F14523", "A9462", "F2137", "F402", "F14274", "F1123", "F148",
//    "M1543", "F8", "F317", "F813", "CTS9713", "F14682", "F7479", "F1754", "CTS1154", "F5970")
//  val snpBeta = ArrayBuffer("F444", "F79", "F46", "F48", "F55", "F152", "F563", "F209", "F2173", "F14521", "CTS3776",
//    "CTS3763", "KM3028", "F5535", "F4249", "F3607", "F823", "CTS335", "L1360", "KM3031", "CTS53",
//    "F1326", "F2903", "CTS4266", "CTS1933", "CTS1346", "F14214", "CTS2056")
//
//  val snpMap = Map(
//    "coreset" -> snpCoreset, "N" -> snpN, "P" -> snpP, "01" -> snp01, "02" -> snp02,
//    "gamma" -> snpGamma, "alpha" -> snpAlpha, "beta" -> snpBeta
//  )
//
//  val spark = init.getScInstance
//  val sc = spark.sparkContext
//
//  import spark.implicits._
//
//  val parent = new File("E:\\testData\\spark_test")
//
//  def test = Action { implicit request =>
//    val startTime = System.currentTimeMillis()
//    //    val rdd=sc.parallelize(Seq("12.5\t18.5","13.5\t17.5"))
//    //    val newRdd=rdd.map(_+rdd.count()).sortBy(_.split("\t")(0).toDouble)
//    //    println(newRdd)
//    //    addLineNumColumn
//    //    filterNull
//    //    confirmYSnp
//    //    filterM231
//    //    doKind()
//    //    doKindByName
//    //        domerge
//    //                calculateDistance
//    //                distanceMerge
//    //    distanceMatrix
////    val file = new File(parent, s"matrix_merge.txt")
////    val df = txt2Df(file, header = false)
////    val renameDf = df.columns.foldLeft(df)((curr, n) => curr.withColumnRenamed(n, n.replaceAll("_", "")))
////    val selectColumns = renameDf.columns.drop(1).map(col(_))
////    val usedColumns = renameDf.columns.drop(1)
////    val newDf = renameDf.select(selectColumns: _*)
////    val rf = new RFormula().setFormula(s"~${usedColumns.mkString(" + ")}").setFeaturesCol("features")
////    val pca = new PCA().setInputCol("features").setOutputCol("outFeatures").setK(2)
////    val featurized = rf.fit(newDf).transform(newDf)
////    val projected = pca.fit(featurized).transform(featurized)
////    val outDf = projected.select(Array("c0", "outFeatures").map(col(_)): _*).map { row =>
////      val vector = row.getAs[DenseVector](1)
////      (row.getInt(0), vector(0), vector(1))
////    }.toDF("index", "pc1", "pc2")
////    outDf.show()
////    val outFile = new File(parent, "outDf.txt")
////    df2Txt(outDf, outFile)
//    println(tool.quickLines.size)
//    println(tool.pathFinderPlus.size)
//    println(tool.pathFinderPlus.union(tool.quickLines).distinct.size)
//
//
//    Ok("success " + Utils.getTime(startTime))
//  }
//
//  def distanceMatrix = {
//    val parent = new File("E:\\testData\\spark_test")
//    val file = new File(parent, s"Y database_3_distance_merge.txt")
//    val lines = FileUtils.readLines(file).asScala
//    val map = mutable.LinkedHashMap[String, Array[String]]()
//    lines.foreach { line =>
//      val columns = line.split("\t")
//      val datas = columns.slice(3, 22)
//      val index = columns(1)
//      map += (index -> datas)
//    }
//    val indexs = map.keys.toBuffer
//    val header = ArrayBuffer("") ++= indexs
//    val weights = ArrayBuffer(4, 3, 2, 4, 3, 20, 8, 7, 27, 2, 5, 2, 1, 2, 3, 1, 0.3, 9, 20)
//    val newLines = ArrayBuffer(header) ++= indexs.zipWithIndex.map { case (index, i) =>
//      if (i % 500 == 0) println(index)
//      ArrayBuffer(index) ++= indexs.map { otherIndex =>
//        val data = map(index)
//        val otherData = map(otherIndex)
//        val tmpDistances = data.zipWithIndex.map { case (v, i) =>
//          (data(i), otherData(i), weights(i))
//        }.withFilter { case (x, y, z) =>
//          StringUtils.isNotBlank(x) && StringUtils.isNotBlank(y)
//        }.withFilter { case (x, y, z) =>
//          Utils.isDouble(x) && Utils.isDouble(y)
//        }.map { case (x, y, z) =>
//          math.abs(x.toDouble - y.toDouble) * z
//        }
//        (tmpDistances.sum / tmpDistances.size).toString
//      }
//    }.toBuffer
//    FileUtils.writeLines(new File(parent, "matrix.txt"), newLines.map(_.mkString("\t")).asJava)
//
//
//  }
//
//  def df2Txt(df: DataFrame, outFile: File) = {
//    val tmpDir = Files.createTempDirectory("tmpDir").toFile
//    df.write.option("delimiter", "\t").option("header", true).mode("overwrite").csv(tmpDir.getAbsolutePath)
//    val hadoopConfig = new Configuration()
//    val hdfs = FileSystem.get(hadoopConfig)
//    hdfs.setWriteChecksum(false)
//    hdfs.setVerifyChecksum(false)
//    FileUtil.fullyDelete(outFile)
//    FileUtil.copyMerge(hdfs, new Path(tmpDir.getAbsolutePath), hdfs, new Path(outFile.getAbsolutePath),
//      true, hadoopConfig, null)
//  }
//
//  def filterM231 = {
//    val file = new File(parent, "Y database_3_y_snp.txt")
//    val rdd = sc.textFile(file.getAbsolutePath)
//    val newRdd = rdd.filter { line =>
//      val columns = line.split("\t")
//
//      def getInfoBuffer(value: String) = {
//        val str = Util.removeQuote(value).replaceAll("\\(.*\\)", "")
//        str.split(",").map { x =>
//          if (x.matches(".+-.+")) {
//            val array = x.split("-")
//            x.split("-")(1)
//          } else x
//        }.flatMap { x =>
//          x.split("\\+")
//        }.flatMap { x =>
//          x.split("\\s+")
//        }
//      }
//
//      val snpStrs = getInfoBuffer(columns(9))
//      val snps = Array("M231", "M231+")
//      snpStrs.exists(x => snps.contains(x))
//    }
//    println(newRdd.count())
//    val outFile = new File(parent, s"Y database_3_M231.txt")
//    rddSave2SingleFile(newRdd, outFile)
//    println("----")
//  }
//
//  def txt2Df(csvFile: File, header: Boolean = true) = {
//    val spark = init.getScInstance
//    import spark.implicits._
//    spark.read.option("header", header).option("delimiter", "\t").
//      option("inferSchema", true).csv(csvFile.getAbsolutePath)
//  }
//
//  def calculateDistance = {
//    val kinds = snpMap.keys.toBuffer
//    kinds.foreach { kind =>
//      println(kind)
//      val startTime = System.currentTimeMillis()
//      val file = new File(parent, s"Y database_3_${kind}.txt")
//      val rdd = sc.textFile(file.getAbsolutePath).map(_.split("\t").map(Util.removeQuote(_)).toBuffer)
//      val rdds = rdd.collect().toBuffer
//      val newRdd = rdd.map { columns =>
//        val otherRdds = rdds - columns
//        val distances = otherRdds.map { otherColumns =>
//          val datas = columns.slice(10, 29)
//          val ortherDatas = otherColumns.slice(10, 29)
//          val weights = ArrayBuffer(4, 3, 2, 4, 3, 20, 8, 7, 27, 2, 5, 2, 1, 2, 3, 1, 0.3, 9, 20)
//          val tmpDistances = datas.indices.map { i =>
//            (datas(i), ortherDatas(i), weights(i))
//          }.withFilter { case (x, y, z) =>
//            StringUtils.isNotBlank(x) && StringUtils.isNotBlank(y)
//          }.withFilter { case (x, y, z) =>
//            Utils.isDouble(x) && Utils.isDouble(y)
//          }.map { case (x, y, z) =>
//            math.abs(x.toDouble - y.toDouble) * z
//          }
//          tmpDistances.sum / tmpDistances.size
//        }
//        val avgDistance = distances.sum / distances.size
//        val minDistance = distances.min
//        val maxDistance = distances.max
//        (columns.take(1) += avgDistance.toString += minDistance.toString += maxDistance.toString ++= columns.drop(1)).mkString("\t")
//      }.sortBy(_.split("\t")(1).toDouble)
//      println(Utils.getTime(startTime))
//      val txtFile = new File(parent, s"Y database_3_${kind}_distance.txt")
//      rddSave2SingleFile(newRdd, txtFile)
//    }
//  }
//
//  def distanceMerge = {
//    val kinds = snpMap.keys.toBuffer
//    val newLines = kinds.flatMap { kind =>
//      val file = new File(parent, s"Y database_3_${kind}_distance.txt")
//      val lines = FileUtils.readLines(file).asScala
//      lines.map { line =>
//        val columns = line.split("\t")
//        s"${kind}\t${columns(0)}\t${columns(1)}\t${columns.slice(11, 31).mkString("\t")}"
//      }
//    }
//    val outFile = new File(parent, s"Y database_3_distance_merge.txt")
//    FileUtils.writeLines(outFile, newLines.asJava)
//  }
//
//  def domerge = {
//    val kinds = snpMap.keys.toBuffer
//    kinds.foreach { kind =>
//      val file = new File(parent, s"Y database_3_${kind}.txt")
//      val rdd = sc.textFile(file.getAbsolutePath)
//      val byNameFile = new File(parent, s"Y database_3_${kind}_by_name.txt")
//      val byNameRdd = sc.textFile(byNameFile.getAbsolutePath)
//      val newRdd = (rdd ++ byNameRdd).distinct.sortBy(_.split("\t")(0).toInt)
//      val outFile = new File(parent, s"Y database_3_${kind}_merge.txt")
//      rddSave2SingleFile(newRdd, outFile)
//    }
//  }
//
//  def rddSave2SingleFile(newRdd: RDD[String], outFile: File) = {
//    val tmpDir = Files.createTempDirectory("tmpDir").toFile
//    FileUtil.fullyDelete(tmpDir)
//    newRdd.saveAsTextFile(tmpDir.getAbsolutePath)
//    val hadoopConfig = new Configuration()
//    val hdfs = FileSystem.get(hadoopConfig)
//    hdfs.setWriteChecksum(false)
//    hdfs.setVerifyChecksum(false)
//    FileUtil.fullyDelete(outFile)
//    FileUtil.copyMerge(hdfs, new Path(tmpDir.getAbsolutePath), hdfs, new Path(outFile.getAbsolutePath), true, hadoopConfig, null)
//  }
//
//  def doKindByName: Unit = {
//    val file = new File(parent, "Y database_3_y_snp.txt")
//    val rdd = sc.textFile(file.getAbsolutePath)
//    val kinds = snpMap.keys.toBuffer
//    println("----")
//    var size = 0L
//    kinds.foreach { kind =>
//      val prefix = kind match {
//        case "alpha" => "(Oα|O3a2c1a-)"
//        case "beta" => "(Oβ|O3a2c1\\*)"
//        case "gamma" => "(Oγ|O3a1c1)"
//        case "02" => "O2"
//        case "01" => "O1"
//        case "P" => "(Q|R)"
//        case "N" => "N"
//        case "coreset" => "无"
//      }
//      val newRdd = rdd.filter { line =>
//        val columns = line.split("\t")
//        val snps = Array(columns(8), columns(9)).map(x => Util.removeQuote(x))
//        snps.exists(_.matches(s"^${prefix}.*$$"))
//      }
//      println("----" + kind + "----")
//      println(newRdd.count())
//      size += newRdd.count()
//      val outFile = new File(s"E:\\testData\\spark_test\\Y database_3_${kind}_by_name.txt")
//      rddSave2SingleFile(newRdd, outFile)
//    }
//    println("----")
//    println(size)
//
//  }
//
//  def doKind(): Unit = {
//    val file = new File(parent, "Y database_3_y_snp.txt")
//    val rdd = sc.textFile(file.getAbsolutePath)
//    val kinds = snpMap.keys.toBuffer
//    println("----")
//    var size = 0L
//    kinds.foreach { kind =>
//      val newRdd = rdd.filter { line =>
//        val columns = line.split("\t")
//
//        def getInfoBuffer(value: String) = {
//          val str = Util.removeQuote(value).replaceAll("\\(.*\\)", "")
//          str.split(",").map { x =>
//            if (x.matches(".+-.+")) {
//              val array = x.split("-")
//              x.split("-")(1)
//            } else x
//          }.flatMap { x =>
//            x.split("\\+")
//          }.flatMap { x =>
//            x.split("\\s+")
//          }
//        }
//
//        val snpStrs = getInfoBuffer(columns(9))
//        val snps = snpMap(kind) ++ snpMap(kind).map(_ + "+")
//        snpStrs.exists(x => snps.contains(x))
//      }
//      println("----" + kind + "----")
//      println(newRdd.count())
//      size += newRdd.count()
//      val outFile = new File(s"E:\\testData\\spark_test\\Y database_3_${kind}.txt")
//      rddSave2SingleFile(newRdd, outFile)
//    }
//    println("----")
//    println(size)
//  }
//
//  def confirmYSnp = {
//    val file = new File(parent, "Y database_3_filter_null.txt")
//    val outFile = new File(parent, "Y database_3_y_snp.txt")
//    val rdd = sc.textFile(file.getAbsolutePath)
//    val newRdd = rdd.filter { line =>
//      val columns = line.split("\t")
//      columns.slice(8, 10).exists(x => StringUtils.isNotBlank(x))
//    }
//    println(newRdd.count())
//    rddSave2SingleFile(newRdd, outFile)
//  }
//
//  def filterNull = {
//    val file = new File(parent, "Y database_3_index.txt")
//    val outFile = new File(parent, "Y database_3_filter_null.txt")
//    val rdd = sc.textFile(file.getAbsolutePath)
//    println(rdd.count())
//    val newRdd = rdd.filter { line =>
//      val columns = line.split("\t")
//      columns.slice(10, 29).filter(x => StringUtils.isBlank(x)).size <= 4
//    }
//    println(newRdd.count())
//    rddSave2SingleFile(newRdd, outFile)
//  }
//
//
//  def addLineNumColumn = {
//    val file = new File(parent, "Y database_3_utf8.txt")
//    val outFile = new File(parent, "Y database_3_index.txt")
//    val newRdd = sc.textFile(file.getAbsolutePath).zipWithIndex().map { case (line, i) =>
//      s"${i + 1}\t${line}"
//    }
//    rddSave2SingleFile(newRdd, outFile)
//  }
//
//  def getOutFile(outFile: File): File = {
//    FileUtil.fullyDelete(outFile)
//    outFile
//  }
//
//  def getOutFile(outPath: String): File = {
//    getOutFile(new File(outPath))
//  }
//
//
//}
