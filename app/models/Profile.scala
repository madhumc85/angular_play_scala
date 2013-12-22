package models

import play.api.libs.json.Json
import play.api.data._
import play.api.data.Forms._
import play.modules.reactivemongo.json.BSONFormats._

import reactivemongo.bson.BSONObjectID

case class Profile(
  _id: BSONObjectID,
  firstName: String,
  lastName: String,
  age: Int,
  email: String,
  phone: Long,
  city: String,
  state: String,
  zip: Int,
  applications: Application,
  appointments: Appointment)

case class Application(
  date: String,
  desc: String)

case class Appointment(
  date: String,
  desc: String)

object ProfileJsonFormats {
  // Generates Writes and Reads for classes thanks to Json Macros
  implicit val applicationFormat = Json.format[Application]
  implicit val appointmentFormat = Json.format[Appointment]
  implicit val profileFormat = Json.format[Profile]
}