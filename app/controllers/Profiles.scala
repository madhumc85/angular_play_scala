package controllers

import models.{Profile, Application, Appointment}
import models.ProfileJsonFormats._

import play.api._
import play.api.data.Form
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats._
import scala.concurrent.Future

// Reactive Mongo imports
import reactivemongo.api._
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONObjectID

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
 object Profiles extends Controller with MongoController {

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */

  // the collection name in mongodb is "profiles"
  def collection: JSONCollection = db.collection[JSONCollection]("profiles")
  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  /** retrieve a profile for the given id as JSON */
  def show(id: String) = Action.async(parse.empty) { request =>
    val futureProfile = collection.find(BSONDocument("_id" -> new BSONObjectID(id))).one[Profile]

    // return JSON
    futureProfile.map { profile => Ok(Json.toJson(profile)) }
  }
  
  /** update a profile for the given id from the JSON body */
  def update(id: String) = Action.async(parse.json) { request =>
    // get JSON data
    val body = request.body
      
    // construct the profile object from the JSON
    val profile = Profile(
      new BSONObjectID(id),
      body.\("firstName").as[String],
      body.\("lastName").as[String],
      body.\("age").as[Int],
      body.\("email").as[String],
      body.\("phone").as[Long],
      body.\("city").as[String],
      body.\("state").as[String],
      body.\("zip").as[Int],
      Application(
        body.\("applications").\("date").as[String],
        body.\("applications").\("desc").as[String]),
      Appointment(
        body.\("appointments").\("date").as[String],
        body.\("appointments").\("desc").as[String]))

    // save to mongo and return Ok
    collection.save(profile).map(_ => Ok)
  }

  /** create a profile from the given JSON */
  def create() = Action.async(parse.json) { request =>
    // get JSON data
    val body = request.body
      
    // construct the profile object from the JSON
    val profile = Profile(
      BSONObjectID.generate,
      body.\("firstName").as[String],
      body.\("lastName").as[String],
      body.\("age").as[Int],
      body.\("email").as[String],
      body.\("phone").as[Long],
      body.\("city").as[String],
      body.\("state").as[String],
      body.\("zip").as[Int],
      Application(
        body.\("applications").\("date").as[String],
        body.\("applications").\("desc").as[String]),
      Appointment(
        body.\("appointments").\("date").as[String],
        body.\("appointments").\("desc").as[String]))

    // insert to mongo and return Ok with the new JSON object
    collection.insert(profile).map(_ => Ok(Json.toJson(profile)))
  }

}