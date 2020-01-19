package controllers

import java.sql.Date

import play.api.data._
import play.api.data.Forms._
import models.Tables._
import org.joda.time.{DateTime, LocalTime}
import play.api.data.format.Formats._
import play.api.data.JodaForms.jodaDate



/**
  * Created by yz on 2018/7/18
  */
case class UserData(name: String, password: String, phone: String)

case class LoginUserData(name: String, password: String)

case class SearchData(units: Option[Seq[String]], sampleTypes: Option[Seq[String]], sexs: Option[Seq[String]],
                      countries: Option[Seq[String]], nations: Option[Seq[String]],startDate:Option[Date],
                      endDate:Option[Date])

case class BasicInfoData(number: String, unit: String, sampleType: String, name: String = "",
                         icNumber: String = "", sex: String, birthDate: Option[Date], country: String,
                         nation: String, residentialPlace: String, address: String = "", culture: String = "",
                         contactWay1: String = "", contactWay2: String = "", comment: String = "", inferSnpKitName: Option[String] = None)

case class UserStrData(kitName:String,map:Map[String,String])

case class NumbersData(numbers: Seq[String])

case class OutData(message: String)

class FormTool {

  case class userNameData(name: String)

  val userNameForm = Form(
    mapping(
      "name" -> text
    )(userNameData.apply)(userNameData.unapply)
  )

  val userForm = Form(
    mapping(
      "name" -> text,
      "password" -> text,
      "phone" -> text
    )(UserData.apply)(UserData.unapply)
  )

  val loginUserForm = Form(
    mapping(
      "name" -> text,
      "password" -> text
    )(LoginUserData.apply)(LoginUserData.unapply)
  )

  case class IdData(id: Int)

  val idForm = Form(
    mapping(
      "id" -> number
    )(IdData.apply)(IdData.unapply)
  )

  case class PhoneData(phone: String)

  val phoneForm = Form(
    mapping(
      "phone" -> text
    )(PhoneData.apply)(PhoneData.unapply)
  )

  case class ValidCodeData(phone: String, code: String)

  val validCodeForm = Form(
    mapping(
      "phone" -> text,
      "code" -> text
    )(ValidCodeData.apply)(ValidCodeData.unapply)
  )

  case class ChangePasswordData(password: String, newPassword: String)

  val changePasswordForm = Form(
    mapping(
      "password" -> text,
      "newPassword" -> text
    )(ChangePasswordData.apply)(ChangePasswordData.unapply)
  )

  case class NewPasswordData(name: String, newPassword: String)

  val newPasswordForm = Form(
    mapping(
      "name" -> text,
      "newPassword" -> text
    )(NewPasswordData.apply)(NewPasswordData.unapply)
  )

  val basicInfoForm = Form(
    mapping(
      "number" -> text,
      "unit" -> text,
      "sampleType" -> text,
      "name" -> text,
      "icNumber" -> text,
      "sex" -> text,
      "birthDate" -> optional(sqlDate),
      "country" -> text,
      "nation" -> text,
      "residentialPlace" -> text,
      "address" -> text,
      "culture" -> text,
      "contactWay1" -> text,
      "contactWay2" -> text,
      "comment" -> text,
      "inferSnpKitName" -> optional(text)
    )(BasicInfoData.apply)(BasicInfoData.unapply)
  )

  case class NumberData(number: String)

  val numberForm = Form(
    mapping(
      "number" -> text
    )(NumberData.apply)(NumberData.unapply)
  )

  val numbersForm = Form(
    mapping(
      "numbers" -> seq(text)
    )(NumbersData.apply)(NumbersData.unapply)
  )

  case class StrData(strKitName: String, strSiteNames: Seq[String], strSiteDatas: Seq[String])

  val strForm = Form(
    mapping(
      "strKitName" -> text,
      "strSiteNames" -> seq(text),
      "strSiteDatas" -> seq(text)
    )(StrData.apply)(StrData.unapply)
  )

  val pattern = "yyyy/mm/dd"

  case class SnpData(snpKitName: String, snpSiteNames: Seq[String], snpSiteDatas: Seq[Option[String]])

  val snpForm = Form(
    mapping(
      "snpKitName" -> text,
      "snpSiteNames" -> seq(text),
      "snpSiteDatas" -> seq(optional(text))
    )(SnpData.apply)(SnpData.unapply)
  )

  case class FileNameData(fileName: String)

  val fileNameForm = Form(
    mapping(
      "fileName" -> text
    )(FileNameData.apply)(FileNameData.unapply)
  )

  val searchForm = Form(
    mapping(
      "units" -> optional(seq(text)),
      "sampleTypes" -> optional(seq(text)),
      "sexs" -> optional(seq(text)),
      "countries" -> optional(seq(text)),
      "nations" -> optional(seq(text)),
      "startDate" -> optional(sqlDate),
      "endDate" -> optional(sqlDate)
    )(SearchData.apply)(SearchData.unapply)
  )


  case class UpdateWeightData(siteNames: Seq[String], values: Seq[Option[Double]])

  val updateWeightForm = Form(
    mapping(
      "siteNames" -> seq(text),
      "values" -> seq(optional(of(doubleFormat)))
    )(UpdateWeightData.apply)(UpdateWeightData.unapply)
  )

  case class SiteNameData(siteName: String)

  val siteNameForm = Form(
    mapping(
      "siteName" -> text
    )(SiteNameData.apply)(SiteNameData.unapply)
  )

  case class Add2DbData(number: String, kind: String)

  val add2DbForm = Form(
    mapping(
      "number" -> text,
      "kind" -> text
    )(Add2DbData.apply)(Add2DbData.unapply)
  )

}
