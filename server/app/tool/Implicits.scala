package tool

import java.io.File

import controllers.{NumbersData, OutData}
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Created by yz on 2019/3/29
  */
object Implicits {

  implicit val inDataFormat = Json.format[NumbersData]
  implicit val outDataFormat = Json.format[OutData]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[NumbersData, OutData]





}
