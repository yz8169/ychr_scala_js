package controllers

import java.io.File

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.stream.Materializer
import dao._
import javax.inject.Inject
import models.Tables._
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{AbstractController, ControllerComponents, RequestHeader, WebSocket}
import tool.Tool
import utils.Utils
import tool.Implicits._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2019/3/13
  */
class UserSnpDataController @Inject()(cc: ControllerComponents, formTool: FormTool, tool: Tool,
                                      implicit val materializer: Materializer, implicit val system: ActorSystem,
                                      userSnpDbDao: UserSnpDbDao, snpDataDao: SnpDataDao,
                                      basicInfoDao: BasicInfoDao) extends AbstractController(cc) {

  def numberCheck = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest.get
    val userId = tool.getUserId
    userSnpDbDao.selectByNumber(userId, data.number).map { optionRow =>
      optionRow match {
        case Some(y) => Ok(Json.obj("valid" -> false))
        case None => Ok(Json.obj("valid" -> true))
      }
    }
  }

  def add2Db = Action.async { implicit request =>
    val data = formTool.add2DbForm.bindFromRequest().get
    val userId = tool.getUserId
    val row = UserSnpDbRow(userId, data.number, data.kind)
    userSnpDbDao.insertOrUpdate(row).map { x =>
      Ok(Json.toJson("success!"))
    }

  }

  def addBatch = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    val userId = tool.getUserId
    val numbers = data.numbers
    snpDataDao.selectAll(userId, numbers).flatMap { snpDatas =>
      val rows = snpDatas.map { snpData =>
        val kind = snpData.kitName
        UserSnpDbRow(userId, snpData.number, kind)
      }
      userSnpDbDao.insertOrUpdates(rows).map { x =>
        Ok(Json.toJson("success!"))
      }
    }

  }

  def inferDetailBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    Ok(views.html.user.snp.inferDetail(data.number))
  }

  def getInferData = Action { implicit request =>
    val userId = tool.getUserId
    val data = formTool.numberForm.bindFromRequest().get
    val file = Tool.getSnpStatFile(userId, data.number)
    val (columnNames, array) = if (file.exists()) Utils.getInfoByFile(file,1) else {
      (mutable.Buffer[String](), mutable.Buffer[Map[String, String]]())
    }
    val json = Json.obj("columnNames" -> columnNames, "array" -> array)
    Ok(json)

  }

  def deletes = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    val userId = tool.getUserId
    userSnpDbDao.deleteByNumbers(userId, data.numbers).map { x =>
      Ok(Json.toJson("success"))
    }

  }

  def getSnpData = Action.async { implicit request =>
    val userId = tool.getUserId
    val data = formTool.numberForm.bindFromRequest().get
    snpDataDao.selectByNumberO(userId, data.number).map { x =>
      val array = x.map { y =>
        Utils.getMapByT(y)
      }.getOrElse(Map[String, String]())
      Ok(Json.toJson(array))
    }
  }

  def manageBefore = Action { implicit request =>
    Ok(views.html.user.userSnpDataManage())
  }

  def getAllUserSnpDb = Action.async { implicit request =>
    val userId = tool.getUserId
    userSnpDbDao.selectAll(userId).flatMap { rows =>
      val userSnpMap = rows.map(x => (x.number, x)).toMap
      val numbers = rows.map(_.number)
      basicInfoDao.selectAll(userId, numbers).map { basics =>
        val basicMap = basics.map(x => x.number -> x).toMap
        val array = numbers.map { number =>
          val basic = basicMap(number)
          val row = userSnpMap(number)
          mutable.LinkedHashMap("number" -> number, "kind" -> row.kind,"unit" -> basic.unit, "name" -> basic.name)
        }
        val columnNames = ArrayBuffer("number","kind","unit","name")
        val json = Json.obj("columnNames" -> columnNames, "array" -> array)
        Ok(json)
      }
    }

  }

  def deleteDataByNumber = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    userSnpDbDao.deleteByNumber(userId, data.number).map { x =>
      Ok(Json.toJson("success"))
    }
  }

  def refreshSnpStat = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val userId = tool.getUserId
    snpDataDao.selectByNumber(userId, data.number).zip(basicInfoDao.selectByNumberSome(userId, data.number)).
      flatMap { case (snpRow, data) =>
        Tool.deleteSnpStatFile(userId, data.number)
        tool.snpStat(snpRow, data.number).map { x =>
          Ok("success!")
        }
      }

  }

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
        val outerF = snpDataDao.selectByNumbers(userId, numbers).zip(basicInfoDao.selectAll(userId, numbers)).
          map { case (snpDatas, basics) =>
            val basicMap = basics.map(x => (x.number -> x)).toMap
            snpDatas.zipWithIndex.map { case (row, tmpI) =>
              val basic = basicMap(row.number)
              Tool.deleteSnpStatFile(userId, basic.number)
              val f = tool.snpStat(row, basic.number)
              Utils.execFuture(f)
              val i = numbers.size - (snpDatas.size) + (tmpI + 1)
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


}
