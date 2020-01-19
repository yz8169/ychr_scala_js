package test

import java.io.File

import org.apache.commons.io.FileUtils

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._
import utils.Implicits._

/**
  * Created by Administrator on 2019/7/19
  */
object CodeStat {

  def main(args: Array[String]): Unit = {


    val arrayBuffer = ArrayBuffer[String]()

    getLines(new File("C:\\workspaceForIDEA\\ychr_scala_js\\server\\app"))
    getLines(new File("C:\\workspaceForIDEA\\ychr_scala_js\\server\\conf"))
    getLines(new File("C:\\workspaceForIDEA\\ychr_scala_js\\shared\\src\\main\\scala\\shared"))
    getLines(new File("C:\\workspaceForIDEA\\ychr_scala_js\\client\\src\\main\\scala\\myJs"))

    println(arrayBuffer.size)
    arrayBuffer.toFile(new File("G:\\code.txt"))


    def getLines(file: File): ArrayBuffer[String] = {
      for (f <- file.listFiles()) {
        if (f.isDirectory) {
          getLines(f)
        } else {
          arrayBuffer ++= FileUtils.readLines(f).asScala
        }
      }
      arrayBuffer
    }


  }

}
