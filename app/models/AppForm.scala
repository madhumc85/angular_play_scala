package models

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.{Logger, Application}

import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.collection.JSONCollection

import reactivemongo.api._
import reactivemongo.bson.BSONObjectID

import scala.concurrent._

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

/**
 * Service used by Controller to get forms data.
 *
 * Uses ReactiveMongo + Play JSON library, using case classes that can be turned 
 * into Json using Reads and Writes.
 */
class AppFormService(mongo: MongoRepo) {
  import AppFormJsonFormats.appFormFormat

  /** list all available forms */
  def listForms = { mongo.findForms }
}