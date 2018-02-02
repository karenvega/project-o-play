package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import models.Event
import models.services.{ AuthTokenService, EventService, UserService }
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.mailer.MailerClient
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents }
import utils.auth.DefaultEnv

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Sign Up` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository implementation.
 * @param authTokenService       The auth token service implementation.
 * @param avatarService          The avatar service implementation.
 * @param passwordHasherRegistry The password hasher registry.
 * @param mailerClient           The mailer client.
 * @param webJarsUtil            The webjar util.
 * @param ex                     The execution context.
 */
class ListEventController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  eventService: EventService,
  authInfoRepository: AuthInfoRepository,
  authTokenService: AuthTokenService,
  avatarService: AvatarService,
  passwordHasherRegistry: PasswordHasherRegistry,
  mailerClient: MailerClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def view = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val event = for {
      event <- eventService.getAll(request.identity.email.getOrElse("karenya11@hotmail.com"))
    } yield {
      event
    }
    event.map(e => Ok(views.html.listEvents(e, request.identity)))
  }

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def detail(uuid: UUID) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val event = for {
      event <- eventService.retrieve(uuid)
    } yield {
      event
    }
    event.map(e => Ok(views.html.detail(e)))
  }

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def delete(uuid: UUID) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val event: Future[List[Event]] = for {
      event <- eventService.retrieve(uuid)
      e <- eventService.delete(event.get)
      events <- eventService.getAll(request.identity.email.getOrElse("karenya11@hotmail.com"))
    } yield {
      events
    }
    event.map(e => Ok(views.html.listEvents(e, request.identity)))
  }
}
