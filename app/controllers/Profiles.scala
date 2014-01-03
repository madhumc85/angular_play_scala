package controllers

import models.ProfileJsonFormats.profileFormat
import models.Model

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

import reactivemongo.bson.BSONObjectID

import securesocial.core.SecureSocial

/**
 * Profiles Controller is invoked both by SecureSocial and Angular API calls.
 */
 object Profiles extends Controller with SecureSocial {

  /** 
   * Check if user profile is new or old and redirect appropriately.
   * SecureSocial will redirect to this after a successful login.
   */
  def check = SecuredAction(parse.empty) { implicit request => 
    Redirect(Model.profiles.buildRedirUrl(request.user.email))
  }

  /** retrieve a profile for the given id as JSON */
  def show(id: String) = SecuredAction(ajaxCall = true).async(parse.empty) { request =>
    if(Model.authorized(id, request.user.email)) {
      // return JSON
      Model.profiles.findById(id).map { profile => Ok(Json.toJson(profile)) }
    } else {
      Model.unauthorized
    }
  }

  /** update a profile for the given id from the JSON body */
  def update(id: String) = SecuredAction(ajaxCall = true).async(parse.json) { request =>
    if(Model.authorized(id, request.user.email)) {
      val profile = Model.profiles.profile(new BSONObjectID(id), request.body)
      // insert to mongo and return Ok with the new JSON object
      Model.profiles.updateById(id, request.body).map(_ => Ok(profile))
    } else {
      Model.unauthorized
    }
  }

}