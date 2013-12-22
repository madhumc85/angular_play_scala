package models

import play.api.libs.json.Json
import play.api.data._
import play.api.data.Forms._
import play.modules.reactivemongo.json.BSONFormats._

import reactivemongo.bson.BSONObjectID

case class AppForm(
  _id: Option[BSONObjectID],
  form: String,
  desc: String,
  fee: Int,
  url: String)

object AppFormJsonFormats {
  // Generates Writes and Reads for classes thanks to Json Macros
  implicit val appFormFormat = Json.format[AppForm]
}