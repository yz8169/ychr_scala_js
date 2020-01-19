package utils

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by yz on 2019/3/29
  */
object Implicits {

  def splitByTab(str: String) = str.split("\t").toBuffer

  implicit class MyFile(val file: File) {

    def lines(encoding: String = "UTF-8"): mutable.Buffer[String] = FileUtils.readLines(file, encoding).asScala

    def lines: mutable.Buffer[String] = {
      val encoding = Utils.detectCoding(file)
      lines(encoding)
    }

    def str = Utils.file2Str(file)

  }

  implicit class MyLines(val lines: mutable.Buffer[String]) {

    def notEmptyLines = lines.filter(x => StringUtils.isNotBlank(x))

    def filterByColumns(f: mutable.Buffer[String] => Boolean) = {
      lines.filter { line =>
        val columns = splitByTab(line)
        f(columns)
      }
    }

    def mapByColumns(n: Int, f: mutable.Buffer[String] => mutable.Buffer[String]): mutable.Buffer[String] = {
      lines.take(n) ++= lines.drop(n).map { line =>
        val columns = splitByTab(line)
        val newColumns = f(columns)
        newColumns.mkString("\t")
      }

    }

    def mapByColumns(f: mutable.Buffer[String] => mutable.Buffer[String]): mutable.Buffer[String] = {
      mapByColumns(0, f)
    }

    def mapOtherByColumns[T](f: mutable.Buffer[String] => T) = {
      lines.map { line =>
        val columns = splitByTab(line)
        f(columns)
      }

    }

    def headers = lines.head.split("\t")

    def toFile(file: File, append: Boolean = false, encoding: String = "UTF-8") = {
      FileUtils.writeLines(file, encoding, lines.asJava, append)
    }

    def uniqByColumnName(columnName: String) = {
      val contentLines = lines.drop(1).map { line =>
        val columns = line.split("\t")
        val map = columns.zip(headers).map { case (value, header) =>
          (header, value)
        }.toMap
        (map(columnName), line)
      }.toMap.values.toBuffer
      ArrayBuffer(lines.head) ++ contentLines

    }

  }

  implicit class MyString(val v: String) {

    def columns = v.split("\t").toBuffer

    def isBlank = v.trim.isEmpty

    def notBlank = {
      val b = isBlank
      !b
    }

    def isDouble: Boolean = {
      try {
        v.toDouble
      } catch {
        case _: Exception =>
          return false
      }
      true
    }

    def toFile(file: File, append: Boolean = false) = {
      FileUtils.writeStringToFile(file, v, append)
    }

  }


}
