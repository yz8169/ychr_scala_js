package dao

import javax.inject.Inject
import models.Tables.BasicInfo
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import models.Tables._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2019/1/15
  */
class UserStrDbDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertOrUpdate(row: UserStrDbRow): Future[Unit] = {
    db.run(UserStrDb.insertOrUpdate(row)).map(_ => ())
  }

  def inserts(rows: Seq[UserStrDbRow]): Future[Unit] = {
    db.run(UserStrDb ++= (rows)).map(_ => ())
  }

  def selectAll(userId: Int): Future[Seq[UserStrDbRow]] = db.run(UserStrDb.filter(_.userId === userId).result)

  def deleteByNumber(userId: Int, number: String): Future[Unit] = db.run(UserStrDb.filter(_.userId === userId).
    filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(userId: Int, numbers: Seq[String]): Future[Unit] = db.run(UserStrDb.filter(_.userId === userId).
    filter(_.number.inSetBind(numbers)).delete).map(_ => ())

  def selectByNumber(userId: Int, number: String): Future[Option[UserStrDbRow]] = db.run(UserStrDb.filter(_.userId === userId).
    filter(_.number === number).result.headOption)

  def selectByNumberSome(userId: Int, number: String) = db.run(UserStrDb.filter(_.userId === userId).
    filter(_.number === number).result.head)

  def selectAll(userId: Int, numbers: Seq[String]) = db.run(UserStrDb.filter(_.userId === userId).
    filter(_.number.inSetBind(numbers)).result)

  def updates(rows: Seq[UserStrDbRow]) = {
    val action = DBIO.sequence {
      rows.map { row =>
        UserStrDb.update(row)
      }
    }.transactionally
    db.run(action).map(_ => ())
  }


}
