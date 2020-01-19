package myJs.user

import myJs.Tool
import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._
import scalatags.Text.all._
//import org.scalajs.jquery.jQuery
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
  * Created by yz on 2019/3/12
  */
@JSExportTopLevel("LoadData")
object LoadData {

  @JSExport("init")
  def init = {
    val options = DatepickerOptions.format(Tool.pattern).language("zh-CN")
    $(".datepicker").datepicker(options)

  }

}
