//package utils
//
//import java.io.File
//
//import javax.inject.{Inject, Singleton}
//import spark.Init
//import com.crealytics.spark.excel._
//
///**
//  * Created by yz on 2018/11/16
//  */
//@Singleton
//class Util @Inject()(init:Init) {
//  val spark = init.getScInstance
//
//  import spark.implicits._
//
//  def xlsx2Df(xlsxFile: File,header:Boolean=true) = {
//    val spark = init.getScInstance
//    import spark.implicits._
//    spark.read.excel(
//      useHeader = header,
//      treatEmptyValuesAsNulls = true,
//      inferSchema = false,
//      addColorColumns = false,
//      maxRowsInMemory = 20
//    ).load(xlsxFile.getAbsolutePath)
//  }
//}
//
//object Util{
//
//  def removeQuote(value: String) = {
//    value.replaceAll("^\"", "").replaceAll("\"$", "")
//  }
//
//}
