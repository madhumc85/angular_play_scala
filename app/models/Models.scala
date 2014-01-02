package models

import play.api.Play
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Results._
import scala.concurrent.Future

/**
 * Implemented by Controllers to call methods on service instances.
 */
trait Models {
  val appFormService = new AppFormService
  val profileService = new ProfileService(Play.current)
  def authorized(id: String,email: Option[String]) = { profileService.authorized(id, email) } 
  val unauthorized = { Future(Forbidden("Not authorized")) }
}