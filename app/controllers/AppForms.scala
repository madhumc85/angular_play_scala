package controllers

import models.AppFormJsonFormats.appFormFormat
import models.Models

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

/**
 * AppForms Controller is invoked by Angular controllers.
 */
 object AppForms extends Controller with Models {

  /** list all forms, called on load and after new profile added */
  def index = Action.async {
    // return JSON
    appFormService.listForms.map { forms => Ok(Json.toJson(forms)) }
  }

}