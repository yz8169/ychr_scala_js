package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2018/11/22
  */
class StrWeightDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[StrWeightRow]): Future[Unit] = {
    db.run(StrWeight ++= rows).map(_ => ())
  }

  def deleteAll: Future[Unit] = db.run(StrWeight.delete).map(_ => ())

  def selectAll(userId: Int): Future[Seq[StrWeightRow]] = db.run(StrWeight.filter(_.userId === userId).result)

  def select(userId: Int, siteName: String): Future[StrWeightRow] = db.run(StrWeight.filter(_.userId === userId).
    filter(_.siteName === siteName).result.head)

  def update(row: StrWeightRow): Future[Unit] = db.run(StrWeight.filter(_.userId === row.userId).
    filter(_.siteName === row.siteName).update(row)).
    map(_ => ())

  def updates(rows: Seq[StrWeightRow]) = {
    val action = {
      val delete = StrWeight.delete
      val insertAll = StrWeight ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }

}
