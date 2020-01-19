package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import tool.Tool

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2018/8/15
  */
class SnpDataDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insert(row: SnpDataRow): Future[Unit] = {
    db.run(SnpData += row).map(_ => ())
  }

  def insertAll(rows: Seq[SnpDataRow]): Future[Unit] = {
    db.run(SnpData ++= rows).map(_ => ())
  }

  def deleteByNumber(userId:Int,number: String): Future[Unit] = db.run(SnpData.filter(_.userId===userId).
    filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(userId:Int,numbers: Seq[String]): Future[Unit] = db.run(SnpData.filter(_.userId===userId).
    filter(_.number.inSetBind(numbers)).delete).map(_ => ())

  def selectByNumber(userId:Int,number: String) = db.run(SnpData.filter(_.userId===userId).
    filter(_.number === number).result.head)

  def selectAll(userId:Int) = db.run(SnpData.filter(_.userId===userId).
   result)

  def selectAll(userId:Int,numbers:Seq[String]) = db.run(SnpData.filter(_.userId===userId).
    filter(_.number.inSetBind(numbers)).result)

  def selectAll = db.run(SnpData.result)

  def selectByNumberO(userId:Int,number: String) = db.run(SnpData.filter(_.userId===userId).
    filter(_.number === number).result.headOption)

  def selectByNumbers(userId:Int,numbers:Seq[String]): Future[Seq[SnpDataRow]] = db.run(SnpData.filter(_.userId===userId).
    filter(_.number.inSetBind(numbers)).result)

  def update(row: SnpDataRow) =  db.run(SnpData.filter(_.userId===row.userId).
    filter(_.number===row.number).update(row))

  def insertOrUpdates(rows: Seq[SnpDataRow]) = {
    val action = {
      val numbers = rows.map(_.number)
      val userId=rows.map(_.userId)
      val delete = SnpData.filter(_.userId.inSetBind(userId)).filter(_.number.inSetBind(numbers)).delete
      val insertAll = SnpData ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }


}
