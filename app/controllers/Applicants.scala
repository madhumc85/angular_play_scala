package controllers

import models.{Applicant, Application, Appointment}
import models.JsonFormats._

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
 object Applicants extends Controller with MongoController {

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */

  // the collection name in mongodb is "applicants"
  def collection: JSONCollection = db.collection[JSONCollection]("applicants")
  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  /** list all applicants, called on load and after new applicant added */
  def index = Action.async {
    // find all applicants from mongo in descending order
    val cursor: Cursor[Applicant] = 
      collection.find(Json.obj()).
      sort(Json.obj("created" -> -1)).
      cursor[Applicant]
    
    // gather all the JsObjects in a list
    val futureApplicantsList: Future[List[Applicant]] = cursor.collect[List]()
    
    // everything's ok! Let's reply with the array
    futureApplicantsList.map { applicants => Ok(applicants.toString) }
  }

  /** create an applicant from the given JSON */
  def create() = Action.async(parse.json) { request =>
    // get JSON data
    val body = request.body
      
    // construct the applicant object from the JSON
    val applicant = Applicant(
      body.\("firstName").as[String],
      body.\("lastName").as[String],
      body.\("age").as[Int],
      body.\("email").as[String],
      body.\("phone").as[Long],
      body.\("city").as[String],
      body.\("state").as[String],
      body.\("zip").as[Int],
      Application(
        body.\("application").\("date").as[Int],
        body.\("application").\("desc").as[String]
      ),
      Appointment(
        body.\("appointment").\("date").as[Int],
        body.\("appointment").\("desc").as[String]
      )
    )

    // insert to mongo and return Ok
    collection.insert(applicant).map(_ => Ok)
  }

}