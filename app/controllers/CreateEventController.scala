package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.CreateEventForm
import models.Event
import models.services.{ AuthTokenService, EventService, UserService }
import org.webjars.play.WebJarsUtil
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.mailer.MailerClient
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents, Request }
import utils.auth.{ DefaultEnv, WithProvider }
import play.api.db._
import play.api.mvc._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Create Event` controller.
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
class CreateEventController @Inject() (
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
   * Views the `Create Event` page.
   *
   * @return The result to display.
   */
  def view = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)) {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      Ok(views.html.createEvent(CreateEventForm.form, request.identity))
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      CreateEventForm.form.bindFromRequest.fold(
        form => Future.successful(BadRequest(views.html.createEvent(form, request.identity))),
        data => {
          val result = Redirect(routes.ListEventController.view()).flashing("info" -> Messages("event.created", ""))
          val event = Event(
            eventID = UUID.randomUUID(),
            userEmail = request.identity.email.getOrElse("karenya11@hotmail.com"),
            name = Some(data.name),
            address = Some(data.address),
            category = Some(data.category),
            startDate = Some(data.startDate),
            endDate = Some(data.endDate),
            location = Some(data.place),
            isVirtual = true
          )
          for {
            event <- eventService.save(event)
          } yield {
            event
          }
          Future.successful(result)
        }
      )
  }
}
