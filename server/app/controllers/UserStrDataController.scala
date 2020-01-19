package controllers

import java.io.File

import akka.actor._
import akka.stream.Materializer
import dao._
import javax.inject.Inject
import models.Tables.UserStrDbRow
import play.api.libs.json.{JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import tool.Tool
import utils.Utils

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2019/1/14
  */
class UserStrDataController @Inject()(cc: ControllerComponents, formTool: FormTool, tool: Tool,
                                      implicit val materializer: Materializer, implicit val system: ActorSystem,
                                      strDataDao: StrDataDao, userStrDbDao: UserStrDbDao,
                                      basicInfoDao: BasicInfoDao, snpDataDao: SnpDataDao) extends AbstractController(cc) {

  def manageBefore = Action { implicit request =>
    Ok(views.html.user.userStrDataManage())
  }

  def add2Db = Action.async { implicit request =>
    val data = formTool.add2DbForm.bindFromRequest().get
    val userId = tool.getUserId
    val row = UserStrDbRow(userId, data.number, data.kind)
    userStrDbDao.insertOrUpdate(row).map { x =>
      Ok(Json.toJson("success!"))
    }

  }

  def addBatch = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    val userId = tool.getUserId
    val numbers = data.numbers
    basicInfoDao.selectAll(userId, numbers).zip(userStrDbDao.selectAll(userId, numbers)).
      flatMap { case (basics, userStrs) =>
        val rows = basics.filter(x => x.inferSnpKitName.isDefined).filter(x => !userStrs.map(_.number).contains(x.number)).
          map { basic =>
            val kind = basic.inferSnpKitName.get.split(";")(0).split(":")(0)
            UserStrDbRow(userId, basic.number, kind)
          }
        userStrDbDao.inserts(rows).map { x =>
          Ok(Json.toJson("success!"))
        }
      }

  }

  def getAllUserStrDb = Action.async { implicit request =>
    val userId = tool.getUserId
    userStrDbDao.selectAll(userId).flatMap { rows =>
      val userStrMap = rows.map(x => (x.number, x)).toMap
      val numbers = rows.map(_.number)
      basicInfoDao.selectAll(userId, numbers).zip(snpDataDao.selectAll(userId, numbers)).map { case (basics, snpDatas) =>
        val basicMap = basics.map(x => x.number -> x).toMap
        val snpDataMap = snpDatas.map(x => x.number -> x).toMap
        val array = numbers.map { number =>
          val row = userStrMap(number)
          val snpKitName = snpDataMap.get(number).map(x => x.kitName).getOrElse("")
          val basic = basicMap(number)
          mutable.LinkedHashMap("number" -> number, "kind" -> row.kind, "unit" -> basic.unit, "name" -> basic.name,
            "snpKitName" -> snpKitName
          )
        }
        val columnNames = ArrayBuffer("number","kind","unit","name","snpKitName")
        val json = Json.obj("columnNames" -> columnNames, "array" -> array)
        Ok(json)
      }
    }

  }

  def deleteDataByNumber = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    userStrDbDao.deleteByNumber(userId, data.number).map { x =>
      Ok(Json.toJson("success"))
    }
  }

  def deletes = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    val userId = tool.getUserId
    userStrDbDao.deleteByNumbers(userId, data.numbers).map { x =>
      Ok(Json.toJson("success"))
    }
  }


  def numberCheck = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest.get
    val userId = tool.getUserId
    userStrDbDao.selectByNumber(userId, data.number).map { optionRow =>
      optionRow match {
        case Some(y) => Ok(Json.obj("valid" -> false))
        case None =>
          Ok(Json.obj("valid" -> true))
      }
    }
  }

  def refreshStrInfer = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    strDataDao.selectByNumber(userId, data.number).zip(basicInfoDao.selectByNumberSome(userId, data.number)).
      flatMap { case (row, data) =>
        tool.strInfer(row, data).map { x =>
          Ok("success!")
        }
      }

  }

  case class OutData(message: String)

  implicit val inDataFormat = Json.format[NumbersData]
  implicit val outDataFormat = Json.format[OutData]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[NumbersData, OutData]

  def batchRefresh = WebSocket.accept[NumbersData, OutData] {
    implicit request =>
      ActorFlow.actorRef(out => MyWebSocketActor.props(out))
  }

  object MyWebSocketActor {
    def props(out: ActorRef)(implicit request: RequestHeader) = Props(new MyWebSocketActor(out))
  }

  class MyWebSocketActor(out: ActorRef)(implicit request: RequestHeader) extends Actor {

    def receive = {
      case data: NumbersData =>
        val numbers = data.numbers
        val userId = tool.getUserId
        val outerF = strDataDao.selectByNumbers(userId, numbers).zip(basicInfoDao.selectAll(userId, numbers)).
          map { case (strDatas, basics) =>
            val basicMap = basics.map(x => (x.number -> x)).toMap
            strDatas.zipWithIndex.map { case (row, tmpI) =>
              val basic = basicMap(row.number)
              val f = tool.strInfer(row, basic)
              Utils.execFuture(f)
              val i = numbers.size - (strDatas.size) + (tmpI + 1)
              val percent = (i * 100 / numbers.size)
              out ! OutData(s"，已完成${percent}%（${i}/${numbers.size}）")
            }
          }
        Utils.execFuture(outerF)
        self ! PoisonPill
      case x =>
        println(x)
    }

  }


  def getStrData = Action.async { implicit request =>
    val userId = tool.getUserId
    val data = formTool.numberForm.bindFromRequest().get
    strDataDao.selectByNumberO(userId, data.number).map { x =>
      val array = x.map { y =>
        Utils.getMapByT(y)
      }.getOrElse(Map[String, String]())
      Ok(Json.toJson(array))
    }
  }

  def getUserStrData = Action.async { implicit request =>
    val userId = tool.getUserId
    val data = formTool.numberForm.bindFromRequest().get
    userStrDbDao.selectByNumberSome(userId, data.number).map { x =>
      val array = Utils.getMapByT(x)
      Ok(Json.toJson(array))
    }

  }


}
