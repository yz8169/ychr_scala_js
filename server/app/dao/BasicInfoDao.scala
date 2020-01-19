package dao

import controllers.SearchData
import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import shared.Shared
import slick.jdbc.JdbcProfile
import utils.Utils

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2018/8/15
  */
class BasicInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  import com.github.tototoshi.slick.MySQLJodaSupport._

  def insert(row: BasicInfoRow): Future[Unit] = {
    db.run(BasicInfo += row).map(_ => ())
  }

  def insertOrUpdate(row: BasicInfoRow) = {
    db.run(BasicInfo.insertOrUpdate(row)).map(_ => ())
  }

  def selectByNumber(userId: Int, number: String): Future[Option[BasicInfoRow]] = db.run(BasicInfo.filter(_.userId === userId).
    filter(_.number === number).result.headOption)

  def selectByNumberSome(userId: Int, number: String) = db.run(BasicInfo.filter(_.userId === userId).
    filter(_.number === number).result.head)

  def selectAll(userId: Int): Future[Seq[BasicInfoRow]] = db.run(BasicInfo.filter(_.userId === userId).
    sortBy(_.uploadTime.desc).result)

  def selectAll = db.run(BasicInfo.sortBy(_.uploadTime.desc).result)

  def selectAll(userId: Int, numbers: Seq[String]) = db.run(BasicInfo.
    filter(_.userId === userId).filter(_.number.inSetBind(numbers)).result)

  def selectAll(userId: Int, data: SearchData) = db.run(BasicInfo.
    filter(_.userId === userId).filter { x =>
    data.units.map { units =>
      x.unit.inSetBind(units)
    }.getOrElse(LiteralColumn(true))
  }.filter { x =>
    data.sampleTypes.map { values =>
      x.sampleType.inSetBind(values)
    }.getOrElse(LiteralColumn(true))
  }.filter { x =>
    data.sexs.map { values =>
      x.sex.inSetBind(values)
    }.getOrElse(LiteralColumn(true))
  }.filter { x =>
    data.countries.map { values =>
      val finalValues = values.map(x => Shared.emptyfy(x))
      x.country.inSetBind(finalValues)
    }.getOrElse(LiteralColumn(true))
  }.filter { x =>
    data.nations.map { values =>
      val finalValues = values.map(x => Shared.emptyfy(x))
      x.nation.inSetBind(finalValues)
    }.getOrElse(LiteralColumn(true))
  }.filter { x =>
    data.startDate.map { value =>
      x.birthdate.map(y => y > value).getOrElse(false)
    }.getOrElse(LiteralColumn(true))
  }.filter { x =>
    data.endDate.map { value =>
      x.birthdate.map(y => y < value).getOrElse(false)
    }.getOrElse(LiteralColumn(true))
  }.result)

  def selectAllInfo(userId: Int): Future[Seq[(BasicInfoRow, Option[StrDataRow], Option[SnpDataRow])]] = db.run(BasicInfo.joinLeft(StrData).
    on((x, y) => x.userId === y.userId && x.number === y.number).joinLeft(SnpData).
    on((x, y) => x._1.userId === y.userId && x._1.number === y.number).map(x => (x._1._1, x._1._2, x._2)).
    filter(_._1.userId === userId).result)


  def deleteByNumber(userId: Int, number: String): Future[Unit] = db.run(BasicInfo.filter(_.userId === userId).
    filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(userId: Int, numbers: Seq[String]): Future[Unit] = db.run(BasicInfo.filter(_.userId === userId).
    filter(_.number.inSetBind(numbers)).delete).map(_ => ())

  def update(row: BasicInfoRow): Future[Unit] = db.run(BasicInfo.filter(_.userId === row.userId).filter(_.number === row.number).
    update(row)).map(_ => ())

  def insertOrUpdates(rows: Seq[BasicInfoRow]) = {
    val action = {
      val numbers = rows.map(_.number)
      val userId = rows.map(_.userId).distinct
      val delete = BasicInfo.filter(_.userId.inSetBind(userId)).filter(_.number.inSetBind(numbers)).delete
      val insertAll = BasicInfo ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }

}
