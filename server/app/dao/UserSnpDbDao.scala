package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2019/1/15
  */
class UserSnpDbDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertOrUpdate(row: UserSnpDbRow) = {
    db.run(UserSnpDb.insertOrUpdate(row)).map(_ => ())
  }

  def selectAll(userId: Int) = db.run(UserSnpDb.filter(_.userId === userId).
    result)

  def selectAll(userId: Int, kind: String) = db.run(UserSnpDb.filter(_.userId === userId).
    filter(_.kind === kind).result)

  def selectAll(userId: Int, numbers: Seq[String]) = db.run(UserSnpDb.filter(_.userId === userId).
    filter(_.number.inSetBind(numbers)).result)

  def deleteByNumber(userId: Int, number: String) = db.run(UserSnpDb.filter(_.userId === userId).
    filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(userId: Int, numbers: Seq[String]) = db.run(UserSnpDb.filter(_.userId === userId).
    filter(_.number.inSetBind(numbers)).delete).map(_ => ())

  def selectByNumber(userId: Int, number: String) = db.run(UserSnpDb.
    filter(_.userId === userId).filter(_.number === number).result.headOption)

  def insertOrUpdates(rows: Seq[UserSnpDbRow]) = {
    val action = DBIO.sequence {
      rows.map { row =>
        UserSnpDb.insertOrUpdate(row)
      }
    }.transactionally
    db.run(action).map(_ => ())
  }


}
