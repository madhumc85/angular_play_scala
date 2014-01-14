package models

import play.api.cache.Cache
import play.api.Play
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.mvc.Results._

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.collection.JSONCollection

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.GetLastError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

import securesocial.core.Identity

/**
 * Singleton for Controllers to call methods on service instances.
 */
object Model {
  val mongoRepo = new MongoRepo
  val cacheRepo = new CacheRepo
  val appForms = new AppFormService(mongoRepo)
  val profiles = new ProfileService(mongoRepo, cacheRepo)
  def authorized(id: String,email: Option[String]) = { profiles.authorized(id, email) } 
  val unauthorized = { Future(Forbidden("Not authorized")) }
}

/**
 * Class containing resources used by services to access MongoDB.
 */
class MongoRepo {
  /** Returns the current instance of the driver. */
  def driver = ReactiveMongoPlugin.driver
  /** Returns the current MongoConnection instance (the connection pool manager). */
  def connection = ReactiveMongoPlugin.connection
  /** Returns the default database (as specified in `application.conf`). */
  def db = ReactiveMongoPlugin.db
  /** Get a JSONCollection (a Collection implementation that is designed to work
   *  with JsObject, Reads and Writes.)
   *  Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   *  the collection reference to avoid potential problems in development with
   *  Play hot-reloading.
   *
   *  Instead of using the default Collection implementation (which interacts with
   *  BSON structures + BSONReader/BSONWriter), we use a specialized
   *  implementation that works with JsObject + Reads/Writes.
   */
  def forms = db.collection[JSONCollection]("forms")
  def profiles = db.collection[JSONCollection]("profiles")
  /** Set the appropriate write concern for saves and inserts.
   *  Detailed info provided here: 
   *  http://reactivemongo.org/releases/nightly/api/index.html#reactivemongo.core.commands.GetLastError
   *  http://docs.mongodb.org/manual/reference/command/getLastError/
   */
  val writeConcern = GetLastError(false, Option(BSONString("majority")), 2000, false)

  /** Find all available forms. */
  def findForms = { forms.find(Json.obj()).cursor[JsObject].collect[List]() }

  /** Find profile by email. */
  def findProfileByEmail(email: String) = {
  	profiles.find(Json.obj("email" -> email)).cursor[JsValue].collect[List]()
  }
  
  /** Find profile by mongo document. */
  def findProfileById(id: String) { 
    profiles.find(BSONDocument("_id" -> BSONObjectID(id))).one[JsValue]
  }

  /** Update profile (actually replaces whole doc). 
   *  TODO writeconcern excluded because it's causing issues on save. Fix.
   */
  def updateProfile(doc: JsValue) = { profiles.save(doc) }

  /** Insert new profile. */
  def insertProfile(doc: JsValue) = { profiles.insert(doc, writeConcern) }
}

/**
 * Case classes for storing cache data.
 */
case class ProfStatCache(
  key: String,
  value: ProfileStatus)

case class IdCache(
  key: String,
  value: Identity)

/**
 * Class containing resources used by services to access the cache.
 */
class CacheRepo {
  val duration = 900  // we set caches to expire after 15 mins

  /** Adds to cache if not already there. */
  def setIfNew(obj: ProfStatCache) = {
    Cache.getOrElse(obj.key, duration) { obj.value }
  }

  def setIfNew(obj: IdCache) = {
    Cache.getOrElse(obj.key, duration) { obj.value }
  }

  /** Adds to cache. */
  def set(obj: ProfStatCache) = {
    Cache.set(obj.key, obj.value, duration)
  }

  def set(obj: IdCache) = {
    Cache.set(obj.key, obj.value, duration)
  }
  
  /** Get from cache. */
  def get[T:ClassTag](key: String) = {
    Cache.getAs[T](key)
  }
  
  /** Remove from cache. */
  def remove(key: String) = {
    Cache.remove(key)
  }
}