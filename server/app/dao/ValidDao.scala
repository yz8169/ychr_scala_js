package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

/**
  * Created by yz on 2018/8/7
  */
class ValidDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def selectByName(name: String): Future[ValidRow] = db.run(Valid.filter(_.name === name).result.head)

}
