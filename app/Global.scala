import play.api.mvc._
import play.api._
import play.filters.csrf._
 
/**
 * Global filter used for CSRF projection.
 */
object Global extends WithFilters(CSRFFilter()) with GlobalSettings