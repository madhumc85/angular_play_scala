package models

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.{Logger, Application}
import play.api.Play.current

import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.collection.JSONCollection

import reactivemongo.api._
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONObjectID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId
import securesocial.core.SocialUser

/**
 * Used for implicit JSON conversions.
 */
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
  applications: Applcation,
  appointments: Appointment)

case class Applcation(
  date: String,
  desc: String)

case class Appointment(
  date: String,
  desc: String)

object ProfileJsonFormats {
  // Generates Writes and Reads for classes thanks to Json Macros
  implicit val applicationFormat = Json.format[Applcation]
  implicit val appointmentFormat = Json.format[Appointment]
  implicit val profileFormat = Json.format[Profile]
}

/**
 * Used for profile caching and authz checks.
 */
case class ProfileStatus(
  _id: String,
  complete: Boolean)

/**
 * Service used by SecureSocial for user profile related data.
 *
 * Methods here are required per the SecureSocial UserService trait.
 *
 * Uses ProfileService for save and find implementations.
 */
class UserService(application: Application) extends UserServicePlugin(application) {
  
  def save(user: Identity): Identity = {
    Model.profiles.save(user)
  }

  def find(id: IdentityId): Option[Identity] = { 
    Model.profiles.find(id)
  }

  def deleteExpiredTokens(): Unit = {}

  def deleteToken(uuid: String): Unit = {}

  def findByEmailAndProvider(email: String,providerId: String): Option[Identity] = {
    Option(SocialUser(IdentityId("",""),"","","",Option(""),None,AuthenticationMethod(""),None,None,None))
  } 

  def findToken(token: String): Option[Token] = { Map[String, Token]().get(token) } 

  def save(token: Token): Unit = {}
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
class ProfileService(application: Application, mongo: MongoRepo, cache: CacheRepo) {
  import ProfileJsonFormats._

  /**
   * Saves base info for a new user. This method gets called by SecureSocial 
   * when a user logs in. It saves the Identity object to the cache, and saves
   * a skeleton profile to mongo if no current doc exists for the user.
   *
   * This queries first for an existing mongo doc using provided email.
   * If a doc is not found a new one is inserted.
   * If a doc is found and it contains an age field, we'll mark this 
   * as a complete profile in the cache. If it doesn't contain age, mark
   * as incomplete in cache.
   */
  def save(user: Identity): Identity = {
    val email = user.email.getOrElse("")
    findByEmail(email).map {
      case List(profile) =>  // found existing doc
        val _id = profile.\("_id").\("$oid").as[String]
        profile.\("age") match {
          case _:JsUndefined =>  // didn't find an age, cache profile status as incomplete
            cache.setIfNew(email, ProfileStatus(_id,false))
          case x =>  // found an age entry, cache profile status as complete
            cache.setIfNew(email, ProfileStatus(_id,true))
        }
      case _ =>  // no existing doc in mongo, insert one
        val _id = BSONObjectID.generate
        insertNew(Json.obj(
          "_id" -> _id,
          "firstName" -> user.firstName,
          "lastName" -> user.lastName,
          "email" -> email))
        // cache profile status as incomplete
        cache.set(email, ProfileStatus(_id.stringify,false))
    }
    // insert SecureSocial identity to cache if not found
    cache.setIfNew(user.identityId.userId, user)
    user
  }

  /**
   * Build redirect URL for user that has successfully logged in.
   * If user has complete profile status, we redirect to their profile page. 
   * If profile status is incomplete, we redirect to create profile form 
   * where they'll fill out and submit profile info.
   */
  def buildRedirUrl(optEmail: Option[String]) = {
    val email = optEmail.getOrElse("")
    var redirUrl = ""
    val profileStatus = cache.get[ProfileStatus](email)
    val id = profileStatus.map(_._id).getOrElse("")
    val complete = profileStatus.map(_.complete).getOrElse(false)
    if(complete) {
      redirUrl += s"/profile/$id"  // take them to their profile
    } else {
      redirUrl += s"/create/$id"  // take them to profile create form
    }
    redirUrl
  }

  /** 
   * Authorization check for API calls that accept an id in the URL.
   * Look up the mongo doc for the user email set by SecureSocial and verify
   * the _id in the doc matches the id in the URL.
   */
  def authorized(id: String,optEmail: Option[String]) = {
    val email = optEmail.getOrElse("")
    var authorized = false
    val authId = cache.get[ProfileStatus](email).map(_._id).getOrElse("")
    if(authId == id) {
      authorized = true
    }
    authorized
  }

  /** Find an existing profile using given email. */
  def findByEmail(email: String) = {
    mongo.profiles.find(Json.obj("email" -> email)).cursor[JsValue].collect[List]()
  }

  /**
   * Finds a user by their user id in the cache. 
   * This method gets called by SecureSocial for each secured action.
   * 15 min expiry is updated each time called.
   */
  def find(id: IdentityId): Option[Identity] = { 
    val optIdentity = cache.get[Identity](id.userId)
    // remove and reset 15 min expiry
    cache.remove(id.userId)
    cache.set(id.userId, optIdentity.getOrElse(dummyIdentity))
    optIdentity
  }

  /** Find an existing profile using given document _id. */
  def findById(id: String) = {
    mongo.profiles.find(BSONDocument("_id" -> new BSONObjectID(id))).one[JsValue]
  }

  /** Update an existing profile for the given document _id. */
  def updateById(id: String,body: JsValue) = {
    val email = body.\("email").as[String]
    cache.remove(email)
    cache.set(email, ProfileStatus(id,true))
    mongo.profiles.save(profile(new BSONObjectID(id), body))
  }

  /** Insert a new profile for the given JSON. */
  def insertNew(body: JsValue) = {
    mongo.profiles.insert(body, mongo.writeConcern)
  }

  /** Construct and return profile object. */
  def profile(id: BSONObjectID,body: JsValue) = {
    // construct the profile object from the JSON
    Json.toJson(Profile(
      id,
      body.\("firstName").as[String],
      body.\("lastName").as[String],
      body.\("age").as[Int],
      body.\("email").as[String],
      body.\("phone").as[Long],
      body.\("city").as[String],
      body.\("state").as[String],
      body.\("zip").as[Int],
      Applcation(
        body.\("applications").\("date").as[String],
        body.\("applications").\("desc").as[String]),
      Appointment(
        body.\("appointments").\("date").as[String],
        body.\("appointments").\("desc").as[String])))
  }

  def dummyIdentity = {
    Option(SocialUser(IdentityId("",""),"","","",Option(""),None,AuthenticationMethod(""),None,None,None))
  }

}