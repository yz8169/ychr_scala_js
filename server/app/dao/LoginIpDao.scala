package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2018/7/19
  */
class LoginIpDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def selectByName(name: String): Future[Seq[LoginIpRow]] = db.run(LoginIp.filter(_.name === name).result)

  def selectByNameAndIp(name: String, ip: String): Future[Option[LoginIpRow]] = db.run(LoginIp.filter(_.name === name).
    filter(_.ip === ip).result.headOption)

  def update(row: LoginIpRow): Future[Unit] = {
    db.run(LoginIp.filter(_.id === row.id).update(row)).map(_ => ())
  }

  def insert(row: LoginIpRow): Future[Unit] = {
    db.run(LoginIp += row).map(_ => ())
  }

  def insertOrUpdate(row: LoginIpRow): Future[Unit] = {
    db.run(LoginIp.insertOrUpdate(row)).map(_ => ())
  }

}
