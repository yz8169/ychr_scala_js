package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Account.schema, BasicInfo.schema, LoginIp.schema, Mode.schema, SnpData.schema, StrData.schema, StrDb.schema, StrWeight.schema, User.schema, UserSnpDb.schema, UserStrDb.schema, Valid.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Account
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param account Database column account SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true)
   *  @param phone Database column phone SqlType(VARCHAR), Length(255,true) */
  case class AccountRow(id: Int, account: String, password: String, phone: String)
  /** GetResult implicit for fetching AccountRow objects using plain SQL queries */
  implicit def GetResultAccountRow(implicit e0: GR[Int], e1: GR[String]): GR[AccountRow] = GR{
    prs => import prs._
    AccountRow.tupled((<<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table account. Objects of this class serve as prototypes for rows in queries. */
  class Account(_tableTag: Tag) extends profile.api.Table[AccountRow](_tableTag, Some("ychr"), "account") {
    def * = (id, account, password, phone) <> (AccountRow.tupled, AccountRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(account), Rep.Some(password), Rep.Some(phone)).shaped.<>({r=>import r._; _1.map(_=> AccountRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column account SqlType(VARCHAR), Length(255,true) */
    val account: Rep[String] = column[String]("account", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column phone SqlType(VARCHAR), Length(255,true) */
    val phone: Rep[String] = column[String]("phone", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Account */
  lazy val Account = new TableQuery(tag => new Account(tag))

  /** Entity class storing rows of table BasicInfo
   *  @param userId Database column user_id SqlType(INT)
   *  @param number Database column number SqlType(VARCHAR), Length(255,true)
   *  @param unit Database column unit SqlType(VARCHAR), Length(255,true)
   *  @param sampleType Database column sample_type SqlType(VARCHAR), Length(255,true)
   *  @param name Database column name SqlType(VARCHAR), Length(255,true), Default()
   *  @param icNumber Database column ic_number SqlType(VARCHAR), Length(255,true)
   *  @param sex Database column sex SqlType(VARCHAR), Length(255,true)
   *  @param birthdate Database column birthdate SqlType(DATE), Default(None)
   *  @param country Database column country SqlType(VARCHAR), Length(255,true)
   *  @param nation Database column nation SqlType(VARCHAR), Length(255,true)
   *  @param residentialPlace Database column residential_place SqlType(VARCHAR), Length(255,true)
   *  @param address Database column address SqlType(VARCHAR), Length(255,true)
   *  @param culture Database column culture SqlType(VARCHAR), Length(255,true)
   *  @param contactWay1 Database column contact_way1 SqlType(VARCHAR), Length(255,true)
   *  @param contactWay2 Database column contact_way2 SqlType(VARCHAR), Length(255,true)
   *  @param comment Database column comment SqlType(TEXT)
   *  @param inferSnpKitName Database column infer_snp_kit_name SqlType(TEXT), Default(None)
   *  @param uploadTime Database column upload_time SqlType(DATETIME) */
  case class BasicInfoRow(userId: Int, number: String, unit: String, sampleType: String, name: String = "", icNumber: String, sex: String, birthdate: Option[java.sql.Date] = None, country: String, nation: String, residentialPlace: String, address: String, culture: String, contactWay1: String, contactWay2: String, comment: String, inferSnpKitName: Option[String] = None, uploadTime: DateTime)
  /** GetResult implicit for fetching BasicInfoRow objects using plain SQL queries */
  implicit def GetResultBasicInfoRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[java.sql.Date]], e3: GR[Option[String]], e4: GR[DateTime]): GR[BasicInfoRow] = GR{
    prs => import prs._
    BasicInfoRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<?[java.sql.Date], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<?[String], <<[DateTime]))
  }
  /** Table description of table basic_info. Objects of this class serve as prototypes for rows in queries. */
  class BasicInfo(_tableTag: Tag) extends profile.api.Table[BasicInfoRow](_tableTag, Some("ychr"), "basic_info") {
    def * = (userId, number, unit, sampleType, name, icNumber, sex, birthdate, country, nation, residentialPlace, address, culture, contactWay1, contactWay2, comment, inferSnpKitName, uploadTime) <> (BasicInfoRow.tupled, BasicInfoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(number), Rep.Some(unit), Rep.Some(sampleType), Rep.Some(name), Rep.Some(icNumber), Rep.Some(sex), birthdate, Rep.Some(country), Rep.Some(nation), Rep.Some(residentialPlace), Rep.Some(address), Rep.Some(culture), Rep.Some(contactWay1), Rep.Some(contactWay2), Rep.Some(comment), inferSnpKitName, Rep.Some(uploadTime)).shaped.<>({r=>import r._; _1.map(_=> BasicInfoRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17, _18.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column number SqlType(VARCHAR), Length(255,true) */
    val number: Rep[String] = column[String]("number", O.Length(255,varying=true))
    /** Database column unit SqlType(VARCHAR), Length(255,true) */
    val unit: Rep[String] = column[String]("unit", O.Length(255,varying=true))
    /** Database column sample_type SqlType(VARCHAR), Length(255,true) */
    val sampleType: Rep[String] = column[String]("sample_type", O.Length(255,varying=true))
    /** Database column name SqlType(VARCHAR), Length(255,true), Default() */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true), O.Default(""))
    /** Database column ic_number SqlType(VARCHAR), Length(255,true) */
    val icNumber: Rep[String] = column[String]("ic_number", O.Length(255,varying=true))
    /** Database column sex SqlType(VARCHAR), Length(255,true) */
    val sex: Rep[String] = column[String]("sex", O.Length(255,varying=true))
    /** Database column birthdate SqlType(DATE), Default(None) */
    val birthdate: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("birthdate", O.Default(None))
    /** Database column country SqlType(VARCHAR), Length(255,true) */
    val country: Rep[String] = column[String]("country", O.Length(255,varying=true))
    /** Database column nation SqlType(VARCHAR), Length(255,true) */
    val nation: Rep[String] = column[String]("nation", O.Length(255,varying=true))
    /** Database column residential_place SqlType(VARCHAR), Length(255,true) */
    val residentialPlace: Rep[String] = column[String]("residential_place", O.Length(255,varying=true))
    /** Database column address SqlType(VARCHAR), Length(255,true) */
    val address: Rep[String] = column[String]("address", O.Length(255,varying=true))
    /** Database column culture SqlType(VARCHAR), Length(255,true) */
    val culture: Rep[String] = column[String]("culture", O.Length(255,varying=true))
    /** Database column contact_way1 SqlType(VARCHAR), Length(255,true) */
    val contactWay1: Rep[String] = column[String]("contact_way1", O.Length(255,varying=true))
    /** Database column contact_way2 SqlType(VARCHAR), Length(255,true) */
    val contactWay2: Rep[String] = column[String]("contact_way2", O.Length(255,varying=true))
    /** Database column comment SqlType(TEXT) */
    val comment: Rep[String] = column[String]("comment")
    /** Database column infer_snp_kit_name SqlType(TEXT), Default(None) */
    val inferSnpKitName: Rep[Option[String]] = column[Option[String]]("infer_snp_kit_name", O.Default(None))
    /** Database column upload_time SqlType(DATETIME) */
    val uploadTime: Rep[DateTime] = column[DateTime]("upload_time")

    /** Primary key of BasicInfo (database name basic_info_PK) */
    val pk = primaryKey("basic_info_PK", (userId, number))
  }
  /** Collection-like TableQuery object for table BasicInfo */
  lazy val BasicInfo = new TableQuery(tag => new BasicInfo(tag))

  /** Entity class storing rows of table LoginIp
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param ip Database column ip SqlType(VARCHAR), Length(255,true)
   *  @param time Database column time SqlType(DATETIME) */
  case class LoginIpRow(id: Int, name: String, ip: String, time: DateTime)
  /** GetResult implicit for fetching LoginIpRow objects using plain SQL queries */
  implicit def GetResultLoginIpRow(implicit e0: GR[Int], e1: GR[String], e2: GR[DateTime]): GR[LoginIpRow] = GR{
    prs => import prs._
    LoginIpRow.tupled((<<[Int], <<[String], <<[String], <<[DateTime]))
  }
  /** Table description of table login_ip. Objects of this class serve as prototypes for rows in queries. */
  class LoginIp(_tableTag: Tag) extends profile.api.Table[LoginIpRow](_tableTag, Some("ychr"), "login_ip") {
    def * = (id, name, ip, time) <> (LoginIpRow.tupled, LoginIpRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(ip), Rep.Some(time)).shaped.<>({r=>import r._; _1.map(_=> LoginIpRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column ip SqlType(VARCHAR), Length(255,true) */
    val ip: Rep[String] = column[String]("ip", O.Length(255,varying=true))
    /** Database column time SqlType(DATETIME) */
    val time: Rep[DateTime] = column[DateTime]("time")
  }
  /** Collection-like TableQuery object for table LoginIp */
  lazy val LoginIp = new TableQuery(tag => new LoginIp(tag))

  /** Entity class storing rows of table Mode
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param test Database column test SqlType(VARCHAR), Length(255,true) */
  case class ModeRow(id: Int, test: String)
  /** GetResult implicit for fetching ModeRow objects using plain SQL queries */
  implicit def GetResultModeRow(implicit e0: GR[Int], e1: GR[String]): GR[ModeRow] = GR{
    prs => import prs._
    ModeRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table mode. Objects of this class serve as prototypes for rows in queries. */
  class Mode(_tableTag: Tag) extends profile.api.Table[ModeRow](_tableTag, Some("ychr"), "mode") {
    def * = (id, test) <> (ModeRow.tupled, ModeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(test)).shaped.<>({r=>import r._; _1.map(_=> ModeRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column test SqlType(VARCHAR), Length(255,true) */
    val test: Rep[String] = column[String]("test", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Mode */
  lazy val Mode = new TableQuery(tag => new Mode(tag))

  /** Entity class storing rows of table SnpData
   *  @param userId Database column user_id SqlType(INT)
   *  @param number Database column number SqlType(VARCHAR), Length(255,true)
   *  @param kitName Database column kit_name SqlType(VARCHAR), Length(255,true)
   *  @param value Database column value SqlType(TEXT) */
  case class SnpDataRow(userId: Int, number: String, kitName: String, value: String)
  /** GetResult implicit for fetching SnpDataRow objects using plain SQL queries */
  implicit def GetResultSnpDataRow(implicit e0: GR[Int], e1: GR[String]): GR[SnpDataRow] = GR{
    prs => import prs._
    SnpDataRow.tupled((<<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table snp_data. Objects of this class serve as prototypes for rows in queries. */
  class SnpData(_tableTag: Tag) extends profile.api.Table[SnpDataRow](_tableTag, Some("ychr"), "snp_data") {
    def * = (userId, number, kitName, value) <> (SnpDataRow.tupled, SnpDataRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(number), Rep.Some(kitName), Rep.Some(value)).shaped.<>({r=>import r._; _1.map(_=> SnpDataRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column number SqlType(VARCHAR), Length(255,true) */
    val number: Rep[String] = column[String]("number", O.Length(255,varying=true))
    /** Database column kit_name SqlType(VARCHAR), Length(255,true) */
    val kitName: Rep[String] = column[String]("kit_name", O.Length(255,varying=true))
    /** Database column value SqlType(TEXT) */
    val value: Rep[String] = column[String]("value")

    /** Primary key of SnpData (database name snp_data_PK) */
    val pk = primaryKey("snp_data_PK", (userId, number))
  }
  /** Collection-like TableQuery object for table SnpData */
  lazy val SnpData = new TableQuery(tag => new SnpData(tag))

  /** Entity class storing rows of table StrData
   *  @param userId Database column user_id SqlType(INT)
   *  @param number Database column number SqlType(VARCHAR), Length(255,true)
   *  @param kitName Database column kit_name SqlType(VARCHAR), Length(255,true)
   *  @param value Database column value SqlType(TEXT) */
  case class StrDataRow(userId: Int, number: String, kitName: String, value: String)
  /** GetResult implicit for fetching StrDataRow objects using plain SQL queries */
  implicit def GetResultStrDataRow(implicit e0: GR[Int], e1: GR[String]): GR[StrDataRow] = GR{
    prs => import prs._
    StrDataRow.tupled((<<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table str_data. Objects of this class serve as prototypes for rows in queries. */
  class StrData(_tableTag: Tag) extends profile.api.Table[StrDataRow](_tableTag, Some("ychr"), "str_data") {
    def * = (userId, number, kitName, value) <> (StrDataRow.tupled, StrDataRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(number), Rep.Some(kitName), Rep.Some(value)).shaped.<>({r=>import r._; _1.map(_=> StrDataRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column number SqlType(VARCHAR), Length(255,true) */
    val number: Rep[String] = column[String]("number", O.Length(255,varying=true))
    /** Database column kit_name SqlType(VARCHAR), Length(255,true) */
    val kitName: Rep[String] = column[String]("kit_name", O.Length(255,varying=true))
    /** Database column value SqlType(TEXT) */
    val value: Rep[String] = column[String]("value")

    /** Primary key of StrData (database name str_data_PK) */
    val pk = primaryKey("str_data_PK", (userId, number))
  }
  /** Collection-like TableQuery object for table StrData */
  lazy val StrData = new TableQuery(tag => new StrData(tag))

  /** Entity class storing rows of table StrDb
   *  @param number Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param kind Database column kind SqlType(VARCHAR), Length(255,true)
   *  @param value Database column value SqlType(LONGTEXT), Length(2147483647,true)
   *  @param familyName Database column family_name SqlType(VARCHAR), Length(255,true)
   *  @param nation Database column nation SqlType(VARCHAR), Length(255,true)
   *  @param address Database column address SqlType(VARCHAR), Length(255,true) */
  case class StrDbRow(number: String, kind: String, value: String, familyName: String, nation: String, address: String)
  /** GetResult implicit for fetching StrDbRow objects using plain SQL queries */
  implicit def GetResultStrDbRow(implicit e0: GR[String]): GR[StrDbRow] = GR{
    prs => import prs._
    StrDbRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table str_db. Objects of this class serve as prototypes for rows in queries. */
  class StrDb(_tableTag: Tag) extends profile.api.Table[StrDbRow](_tableTag, Some("ychr"), "str_db") {
    def * = (number, kind, value, familyName, nation, address) <> (StrDbRow.tupled, StrDbRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(number), Rep.Some(kind), Rep.Some(value), Rep.Some(familyName), Rep.Some(nation), Rep.Some(address)).shaped.<>({r=>import r._; _1.map(_=> StrDbRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val number: Rep[String] = column[String]("number", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column kind SqlType(VARCHAR), Length(255,true) */
    val kind: Rep[String] = column[String]("kind", O.Length(255,varying=true))
    /** Database column value SqlType(LONGTEXT), Length(2147483647,true) */
    val value: Rep[String] = column[String]("value", O.Length(2147483647,varying=true))
    /** Database column family_name SqlType(VARCHAR), Length(255,true) */
    val familyName: Rep[String] = column[String]("family_name", O.Length(255,varying=true))
    /** Database column nation SqlType(VARCHAR), Length(255,true) */
    val nation: Rep[String] = column[String]("nation", O.Length(255,varying=true))
    /** Database column address SqlType(VARCHAR), Length(255,true) */
    val address: Rep[String] = column[String]("address", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table StrDb */
  lazy val StrDb = new TableQuery(tag => new StrDb(tag))

  /** Entity class storing rows of table StrWeight
   *  @param userId Database column user_id SqlType(INT)
   *  @param siteName Database column site_name SqlType(VARCHAR), Length(255,true)
   *  @param value Database column value SqlType(DOUBLE), Default(None) */
  case class StrWeightRow(userId: Int, siteName: String, value: Option[Double] = None)
  /** GetResult implicit for fetching StrWeightRow objects using plain SQL queries */
  implicit def GetResultStrWeightRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Double]]): GR[StrWeightRow] = GR{
    prs => import prs._
    StrWeightRow.tupled((<<[Int], <<[String], <<?[Double]))
  }
  /** Table description of table str_weight. Objects of this class serve as prototypes for rows in queries. */
  class StrWeight(_tableTag: Tag) extends profile.api.Table[StrWeightRow](_tableTag, Some("ychr"), "str_weight") {
    def * = (userId, siteName, value) <> (StrWeightRow.tupled, StrWeightRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(siteName), value).shaped.<>({r=>import r._; _1.map(_=> StrWeightRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column site_name SqlType(VARCHAR), Length(255,true) */
    val siteName: Rep[String] = column[String]("site_name", O.Length(255,varying=true))
    /** Database column value SqlType(DOUBLE), Default(None) */
    val value: Rep[Option[Double]] = column[Option[Double]]("value", O.Default(None))

    /** Primary key of StrWeight (database name str_weight_PK) */
    val pk = primaryKey("str_weight_PK", (userId, siteName))
  }
  /** Collection-like TableQuery object for table StrWeight */
  lazy val StrWeight = new TableQuery(tag => new StrWeight(tag))

  /** Entity class storing rows of table User
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true)
   *  @param phone Database column phone SqlType(VARCHAR), Length(255,true)
   *  @param createTime Database column create_time SqlType(DATETIME) */
  case class UserRow(id: Int, name: String, password: String, phone: String, createTime: DateTime)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String], e2: GR[DateTime]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[DateTime]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends profile.api.Table[UserRow](_tableTag, Some("ychr"), "user") {
    def * = (id, name, password, phone, createTime) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(password), Rep.Some(phone), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column phone SqlType(VARCHAR), Length(255,true) */
    val phone: Rep[String] = column[String]("phone", O.Length(255,varying=true))
    /** Database column create_time SqlType(DATETIME) */
    val createTime: Rep[DateTime] = column[DateTime]("create_time")

    /** Uniqueness Index over (name) (database name name_uniq) */
    val index1 = index("name_uniq", name, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))

  /** Entity class storing rows of table UserSnpDb
   *  @param userId Database column user_id SqlType(INT)
   *  @param number Database column number SqlType(VARCHAR), Length(255,true)
   *  @param kind Database column kind SqlType(VARCHAR), Length(255,true) */
  case class UserSnpDbRow(userId: Int, number: String, kind: String)
  /** GetResult implicit for fetching UserSnpDbRow objects using plain SQL queries */
  implicit def GetResultUserSnpDbRow(implicit e0: GR[Int], e1: GR[String]): GR[UserSnpDbRow] = GR{
    prs => import prs._
    UserSnpDbRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table user_snp_db. Objects of this class serve as prototypes for rows in queries. */
  class UserSnpDb(_tableTag: Tag) extends profile.api.Table[UserSnpDbRow](_tableTag, Some("ychr"), "user_snp_db") {
    def * = (userId, number, kind) <> (UserSnpDbRow.tupled, UserSnpDbRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(number), Rep.Some(kind)).shaped.<>({r=>import r._; _1.map(_=> UserSnpDbRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column number SqlType(VARCHAR), Length(255,true) */
    val number: Rep[String] = column[String]("number", O.Length(255,varying=true))
    /** Database column kind SqlType(VARCHAR), Length(255,true) */
    val kind: Rep[String] = column[String]("kind", O.Length(255,varying=true))

    /** Primary key of UserSnpDb (database name user_snp_db_PK) */
    val pk = primaryKey("user_snp_db_PK", (userId, number))
  }
  /** Collection-like TableQuery object for table UserSnpDb */
  lazy val UserSnpDb = new TableQuery(tag => new UserSnpDb(tag))

  /** Entity class storing rows of table UserStrDb
   *  @param userId Database column user_id SqlType(INT)
   *  @param number Database column number SqlType(VARCHAR), Length(255,true)
   *  @param kind Database column kind SqlType(VARCHAR), Length(255,true) */
  case class UserStrDbRow(userId: Int, number: String, kind: String)
  /** GetResult implicit for fetching UserStrDbRow objects using plain SQL queries */
  implicit def GetResultUserStrDbRow(implicit e0: GR[Int], e1: GR[String]): GR[UserStrDbRow] = GR{
    prs => import prs._
    UserStrDbRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table user_str_db. Objects of this class serve as prototypes for rows in queries. */
  class UserStrDb(_tableTag: Tag) extends profile.api.Table[UserStrDbRow](_tableTag, Some("ychr"), "user_str_db") {
    def * = (userId, number, kind) <> (UserStrDbRow.tupled, UserStrDbRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(number), Rep.Some(kind)).shaped.<>({r=>import r._; _1.map(_=> UserStrDbRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column number SqlType(VARCHAR), Length(255,true) */
    val number: Rep[String] = column[String]("number", O.Length(255,varying=true))
    /** Database column kind SqlType(VARCHAR), Length(255,true) */
    val kind: Rep[String] = column[String]("kind", O.Length(255,varying=true))

    /** Primary key of UserStrDb (database name user_str_db_PK) */
    val pk = primaryKey("user_str_db_PK", (userId, number))
  }
  /** Collection-like TableQuery object for table UserStrDb */
  lazy val UserStrDb = new TableQuery(tag => new UserStrDb(tag))

  /** Entity class storing rows of table Valid
   *  @param name Database column name SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param value Database column value SqlType(VARCHAR), Length(255,true) */
  case class ValidRow(name: String, value: String)
  /** GetResult implicit for fetching ValidRow objects using plain SQL queries */
  implicit def GetResultValidRow(implicit e0: GR[String]): GR[ValidRow] = GR{
    prs => import prs._
    ValidRow.tupled((<<[String], <<[String]))
  }
  /** Table description of table valid. Objects of this class serve as prototypes for rows in queries. */
  class Valid(_tableTag: Tag) extends profile.api.Table[ValidRow](_tableTag, Some("ychr"), "valid") {
    def * = (name, value) <> (ValidRow.tupled, ValidRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(name), Rep.Some(value)).shaped.<>({r=>import r._; _1.map(_=> ValidRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column name SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val name: Rep[String] = column[String]("name", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column value SqlType(VARCHAR), Length(255,true) */
    val value: Rep[String] = column[String]("value", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Valid */
  lazy val Valid = new TableQuery(tag => new Valid(tag))
}
