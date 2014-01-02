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
 * Service used by SecureSocial and Controllers for user profile related data.
 *
 * Uses ReactiveMongo + Play JSON library, using case classes that can be turned 
 * into Json using Reads and Writes.
 *
 * Instead of using the default Collection implementation (which interacts with
 * BSON structures + BSONReader/BSONWriter), we use a specialized
 * implementation that works with JsObject + Reads/Writes.
 *
 * Of course, you can still use the default Collection implementation
 * (BSONCollection.) See ReactiveMongo examples to learn how to use it.
 */
class AppFormService extends Mongo {
  import AppFormJsonFormats.appFormFormat

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def collection: JSONCollection = db.collection[JSONCollection]("forms")

  /** list all available forms */
  def listForms = {
    // find all forms from mongo
    collection.find(Json.obj()).cursor[AppForm].collect[List]()
  }

}