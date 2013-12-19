package models

import java.util.Date

import play.api.libs.json.Json
import play.api.data._
import play.api.data.Forms._

case class Applicant(
  firstName: String,
  lastName: String,
  age: Int,
  email: String,
  phone: String,
  city: String,
  state: String,
  zip: Int,
  applications: Application,
  appointments: Appointment)

case class Application(
  date: Date,
  desc: String)

case class Appointment(
  date: Date,
  desc: String)

object JsonFormats {
  // Generates Writes and Reads for classes thanks to Json Macros
  implicit val applicationFormat = Json.format[Application]
  implicit val appointmentFormat = Json.format[Appointment]
  implicit val applicantFormat = Json.format[Applicant]
}