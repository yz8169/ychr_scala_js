package dao

import javax.inject.{Inject, Singleton}
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2018/11/28
  */
class StrDbDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[StrDbRow]): Future[Unit] = {
    db.run(StrDb ++= rows).map(_ => ())
  }

  def deleteAll: Future[Unit] = db.run(StrDb.delete).map(_ => ())

  def selectAll: Future[Seq[StrDbRow]] = db.run(StrDb.result)

  def selectAll(numbers: Seq[String]) = db.run(StrDb.
    filter(_.number.inSetBind(numbers)).result)

  def updates(rows: Seq[StrDbRow]): Future[Unit] = {
    val action = {
      val delete = StrDb.delete
      val insertAll = StrDb ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }


}
