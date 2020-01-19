package utils

import java.util.concurrent.TimeUnit

import slick.codegen.SourceCodeGenerator
import slick.jdbc.MySQLProfile
import slick.jdbc.meta.MTable
import slick.{model => m}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
  * Created by yz on 2017/5/5.
  */
object SlickCodegen {
  def main(args: Array[String]): Unit = {
    val slickDriver = "slick.jdbc.MySQLProfile"
    val url = "jdbc:mysql://localhost:3306/ychr?useUnicode=true&characterEncoding=UTF-8"
    val jdbcDriver = "com.mysql.jdbc.Driver"
    val user = "root"
    val password = "Yingfei123"

    val db = MySQLProfile.api.Database.forURL(url, user, password)
    val dbio = MySQLProfile.createModel()
    val model = db.run(dbio)
    val future: Future[SourceCodeGenerator] = model.map(model => new SourceCodeGenerator(model) {
      override def code = "import com.github.tototoshi.slick.MySQLJodaSupport._\n" +
        "import org.joda.time.DateTime\n" + super.code
      override def Table = new Table(_) {
        override def Column = new Column(_) {
          override def rawType = model.tpe match {
            case "java.sql.Timestamp" => "DateTime" // kill j.s.Timestamp
            case _ =>
              super.rawType
          }
        }
      }
    })
    val codegen: SourceCodeGenerator = Await.result(future, Duration.create(5, TimeUnit.MINUTES))
    codegen.writeToFile(slickDriver, "server/app", "models", "Tables", "Tables.scala")
  }


}
