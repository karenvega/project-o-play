package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ModifyEventForm
import forms.ModifyEventForm.Data
import models.Event
import models.services.{ AuthTokenService, EventService, UserService }
import org.webjars.play.WebJarsUtil
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.mailer.MailerClient
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents }
import utils.auth.{ DefaultEnv, WithProvider }

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
class ModifyEventController @Inject() (
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
   * Views the `Sign Up` page.
   *
   * @return The result to display.
   */
  def view(uUID: UUID) = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      {
        val event = for {
          event <- eventService.retrieve(uUID)
        } yield {
          event
        }
        event.map { event =>
          val e = event.get
          val data = Data(e.name.get, e.location.get, e.address.get, e.startDate.get, e.endDate.get, e.isVirtual.toString, e.category.get)
          Ok(views.html.modifyEvent(ModifyEventForm.form.fill(data), request.identity, e))
        }
      }
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit(uUID: UUID) = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      {
        val event = for {
          event <- eventService.retrieve(uUID)
        } yield {
          event
        }
        ModifyEventForm.form.bindFromRequest.fold(
          form => event.map(e => BadRequest(views.html.modifyEvent(form, request.identity, e.get))),
          data => {
            val result = Redirect(routes.ListEventController.view())
            val event = Event(
              eventID = uUID,
              userEmail = request.identity.email.toString,
              name = Some(data.name),
              address = Some(data.address),
              category = Some(data.category),
              startDate = Some(data.startDate),
              endDate = Some(data.endDate),
              location = Some(data.place),
              isVirtual = if (data.`type` == "Virtual") true else false
            )
            for {
              event <- eventService.update(event)
            } yield {
              event
            }
            Future.successful(result)
          }
        )
      }
  }
}
