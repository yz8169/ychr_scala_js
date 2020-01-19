package test

import java.io.File

import com.typesafe.config.ConfigFactory

/**
  * Created by yz on 2019/3/14
  */
object DSL {
  def main(args: Array[String]): Unit = {
    val file=new File("D:\\workspaceForIDEA\\ychr_scala_js\\server\\app\\test\\tmp.txt")
    val config = ConfigFactory.parseFile(file)
    println(config)
    val rs=config.getString("yz")
    println(rs)
  }


}


