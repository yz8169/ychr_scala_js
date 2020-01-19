package dao

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.Tables._
import tool.Tool

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2018/8/15
  */
class StrDataDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insert(row: StrDataRow): Future[Unit] = {
    db.run(StrData += row).map(_ => ())
  }

  def insertAll(rows: Seq[StrDataRow]): Future[Unit] = {
    db.run(StrData ++= rows).map(_ => ())
  }

  def deleteByNumber(userId:Int,number: String): Future[Unit] = db.run(StrData.filter(_.userId===userId).
    filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(userId:Int,numbers: Seq[String]): Future[Unit] = db.run(StrData.filter(_.userId===userId).
    filter(_.number.inSetBind(numbers)).delete).map(_ => ())

  def selectByNumber(userId:Int,number: String) = db.run(StrData.filter(_.userId===userId).
    filter(_.number === number).result.head)

  def selectByNumberO(userId:Int,number: String) = db.run(StrData.filter(_.userId===userId).
    filter(_.number === number).result.headOption)

  def selectByNumbers(userId:Int,numbers: Seq[String]): Future[Seq[StrDataRow]] = db.run(StrData.filter(_.userId===userId).
    filter(_.number.inSetBind(numbers)).result)

  def selectAll(userId:Int) = db.run(StrData.filter(_.userId===userId).result)

  def selectAll(userId:Int,numbers:Seq[String]) = db.run(StrData.
    filter(_.userId===userId).filter(_.number.inSetBind(numbers)).result)

  def selectAll = db.run(StrData.result)

  def update(row: StrDataRow) =  db.run(StrData.filter(_.userId===row.userId).
    filter(_.number===row.number).update(row))

  def insertOrUpdates(rows: Seq[StrDataRow]) = {
    val action = {
      val numbers = rows.map(_.number)
      val userId = rows.map(_.userId).distinct
      val delete = StrData.filter(_.userId.inSetBind(userId)).filter(_.number.inSetBind(numbers)).delete
      val insertAll = StrData ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }


}
