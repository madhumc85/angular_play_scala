package controllers

import models.AppForm
import models.AppFormJsonFormats._

import play.api._
import play.api.data.Form
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import scala.concurrent.Future

// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

/*
 * Example using ReactiveMongo + Play JSON library,
 * using case classes that can be turned into Json using Reads and Writes.
 *
 * Instead of using the default Collection implementation (which interacts with
 * BSON structures + BSONReader/BSONWriter), we use a specialized
 * implementation that works with JsObject + Reads/Writes.
 *
 * Of course, you can still use the default Collection implementation
 * (BSONCollection.) See ReactiveMongo examples to learn how to use it.
 */
 object AppForms extends Controller with MongoController {

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */

  // the collection name in mongodb is "forms"
  def collection: JSONCollection = db.collection[JSONCollection]("forms")
  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  /** list all forms, called on load and after new profile added */
  def index = Action.async {
    // find all forms from mongo
    val cursor: Cursor[AppForm] = collection.find(Json.obj()).cursor[AppForm]

    // gather all the JsObjects in a list
    val futureFormsList: Future[List[AppForm]] = cursor.collect[List]()
    
    // return JSON
    futureFormsList.map { forms => Ok(Json.toJson(forms)) }
  }

}