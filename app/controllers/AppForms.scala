package controllers

import models.AppFormJsonFormats.appFormFormat
import models.Model

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

/**
 * AppForms Controller is invoked by Angular controllers.
 */
 object AppForms extends Controller {

  /** list all forms, called on load and after new profile added */
  def index = Action.async {
    // return JSON
    Model.appForms.listForms.map { forms => Ok(Json.toJson(forms)) }
  }

}