package models

import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api._
import reactivemongo.bson.BSONString
import reactivemongo.core.commands.GetLastError
import scala.concurrent.{ Future, ExecutionContext }

/** A mixin for classes that will provide MongoDB actions. */
trait Mongo {
  
  /** Returns the current instance of the driver. */
  def driver = ReactiveMongoPlugin.driver
  /** Returns the current MongoConnection instance (the connection pool manager). */
  def connection = ReactiveMongoPlugin.connection
  /** Returns the default database (as specified in `application.conf`). */
  def db = ReactiveMongoPlugin.db
  /** Set the appropriate write concern for saves and inserts.
   *  Detailed info provided here: 
   *  http://reactivemongo.org/releases/nightly/api/index.html#reactivemongo.core.commands.GetLastError
   *  http://docs.mongodb.org/manual/reference/command/getLastError/
   */
   val writeConcern = GetLastError(false, Option(BSONString("majority")), 2000, false)

}