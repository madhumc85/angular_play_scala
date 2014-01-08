package test

import models.AppFormService
import models.MongoRepo

import org.specs2.matcher.AnyMatchers._ 
import org.specs2.mock._
import org.specs2.mutable._

import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
 * Specs2 tests with Mockito.
 */
class AppFormSpec extends Specification with Mockito {

  "AppFormService#listForms" should {
    "return list of JSON forms" in {
      val mongoRepo = mock[MongoRepo]
      val data = Future { 
        List(
          Json.obj("_id" -> Json.obj("$oid" -> "52cc677d06f8fd8a99bcf267"),
            "form" -> "N-400","desc" -> "Apply for Citizenship","fee" -> 595.0,
            "url" -> "http://www.uscis.gov/n-400"),
          Json.obj("_id" -> Json.obj("$oid" -> "52cc677d06f8fd8a99bcf268"),
            "form" -> "I-485","desc" -> "Apply for a Green Card","fee" -> 985.0,
            "url" -> "http://www.uscis.gov/i-485")) }
      
      mongoRepo.findForms returns data

      val appFormService = new AppFormService(mongoRepo)
      appFormService.listForms mustEqual data
    }

  }
  
}