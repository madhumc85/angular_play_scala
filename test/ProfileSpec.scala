package test

import models.CacheRepo
import models.MongoRepo
import models.ProfileService
import models.ProfileStatus

import org.specs2.matcher.AnyMatchers._ 
import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.specification.Scope

import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.test._
import play.api.test.Helpers._

import reactivemongo.bson.BSONObjectID

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

import securesocial.core._
import securesocial.core.IdentityId
import securesocial.core.SocialUser

/**
 *  Setup trait used by the tests for mocks and vals.
 */
trait setup extends Scope with Mockito {
  val mongoRepo = mock[MongoRepo]
  val cacheRepo = mock[CacheRepo]
  val _id = new BSONObjectID("12345")
  val id = _id.stringify
  val userId = "123"
  val email = "foo@foo.com"
  val name = "foo"
  val user = 
    SocialUser(
      IdentityId(userId,"google"),name,name,name+" "+name,Option(email),None,
        AuthenticationMethod(""),None,None,None)
  val incompleteProfile = Json.obj(
    "_id" -> Json.obj("$oid" -> id),
    "firstName" -> name,
    "lastName" -> name,
    "email" -> email)
  val completeProfile = Json.obj(
    "_id" -> Json.obj("$oid" -> id),
    "firstName" -> name,
    "lastName" -> name,
    "age" -> 66,
    "email" -> email,
    "phone" -> 7777777777L,
    "city" -> "foocity",
    "state" -> "ST",
    "zip" -> 22222,
    "applications" -> Json.obj(
      "date" -> "01/01/2014",
      "desc" -> "foobar"),
    "appointments" -> Json.obj(
      "date" -> "01/01/2014",
      "desc" -> "foobar"))
  val incompleteProfileStatus = ProfileStatus(id,false)
  val completeProfileStatus = ProfileStatus(id,true)
  val profileRedirUrl = s"/profile/$id"
  val createRedirUrl = s"/create/$id"
}

/**
 *  Specs2 test cases.
 */
class ProfileSpec extends Specification {

  "ProfileService#save" should {
    "create incomplete profile in mongo for brand new user" in new setup {
      
      val profileService = new ProfileService(mongoRepo, cacheRepo) {
        override def findByEmail(email: String) = {
          Future { List[JsValue]() }  // brand new user, so no data in mongo for user
        }
        override def createId = { _id }
      }
      val returnedUser = profileService.save(user)
      there was one(mongoRepo).insertProfile(incompleteProfile)
      there was one(cacheRepo).setIfNew(userId, user)
      returnedUser mustEqual user
    }
    "set incomplete profile status in cache for new user with existing base profile in mongo" in new setup {
      val profileService = new ProfileService(mongoRepo, cacheRepo) {
        override def findByEmail(email: String) = {
          Future { List[JsValue](incompleteProfile) }  // existing user with incomplete profile
        }
      }
      val returnedUser = profileService.save(user)
      there was one(cacheRepo).setIfNew(email, incompleteProfileStatus)
      there was one(cacheRepo).setIfNew(userId, user)
      returnedUser mustEqual user
    }
    "set complete profile status in cache for new user with existing full profile in mongo" in new setup {
      val profileService = new ProfileService(mongoRepo, cacheRepo) {
        override def findByEmail(email: String) = {
          Future { List[JsValue](completeProfile) }  // existing user with complete profile
        }
      }
      val returnedUser = profileService.save(user)
      there was one(cacheRepo).setIfNew(email, completeProfileStatus)
      there was one(cacheRepo).setIfNew(userId, user)
      returnedUser mustEqual user
    }
  }

  "ProfileService#buildRedirUrl" should {
    "return profile URL string when user has complete profile" in new setup {

      val profileService = new ProfileService(mongoRepo, cacheRepo)
      cacheRepo.get[ProfileStatus](email: String) returns Option(completeProfileStatus)
      val returnedUrl = profileService.buildRedirUrl(Option(email))
      returnedUrl mustEqual profileRedirUrl
    }
    "return create profile URL string when user has incomplete profile" in new setup {

      val profileService = new ProfileService(mongoRepo, cacheRepo)
      cacheRepo.get[ProfileStatus](email: String) returns Option(incompleteProfileStatus)
      val returnedUrl = profileService.buildRedirUrl(Option(email))
      returnedUrl mustEqual createRedirUrl
    }
  }

  "ProfileService#authorize" should {
    "return true when passed id matches id in cache" in new setup {

      val profileService = new ProfileService(mongoRepo, cacheRepo)
      cacheRepo.get[ProfileStatus](email: String) returns Option(completeProfileStatus)
      val authorized = profileService.authorized(id, Option(email))
      authorized mustEqual true
    }
    "return false when passed id doesn't match id in cache" in new setup {

      val profileService = new ProfileService(mongoRepo, cacheRepo)
      cacheRepo.get[ProfileStatus](email: String) returns Option(completeProfileStatus)
      val authorized = profileService.authorized("6789", Option(email))
      authorized mustEqual false
    }
  }
  
}