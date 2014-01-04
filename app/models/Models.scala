package models

import play.api.cache.Cache
import play.api.Play
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.mvc.Results._

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection

import reactivemongo.api._
import reactivemongo.bson.BSONString
import reactivemongo.bson.BSONObjectID
import reactivemongo.core.commands.GetLastError

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
 * Singleton for Controllers to call methods on service instances.
 */
object Model {
  val mongoRepo = new MongoRepo
  val cacheRepo = new CacheRepo
  val appForms = new AppFormService(mongoRepo)
  val profiles = new ProfileService(Play.current, mongoRepo, cacheRepo)
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
}

/**
 * Class containing resources used by services to access the cache.
 */
class CacheRepo {
  val duration = 900  // we set caches to expire after 15 mins

  /** Adds to cache if not already there. */
  def setIfNew[T:ClassTag](key: String, value: T) = {
  	Cache.getOrElse(key, duration) { value }
  }

  /** Adds to cache. */
  def set[T:ClassTag](key: String, value: T) = {
  	Cache.set(key, value, duration)
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