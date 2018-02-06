package utils.auth

import javax.inject.Inject

import com.mohiva.play.silhouette.api.crypto.Base64
import com.mohiva.play.silhouette.api.exceptions.ConfigurationException
import com.mohiva.play.silhouette.api.{ Logger, LoginInfo, RequestProvider }
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ Credentials, PasswordHasherRegistry, PasswordInfo }
import com.mohiva.play.silhouette.impl.providers.PasswordProvider
import com.mohiva.play.silhouette.impl.providers.PasswordProvider.{ HasherIsNotRegistered, PasswordDoesNotMatch, PasswordInfoNotFound }
import play.api.http.HeaderNames
import play.api.mvc.{ Request, RequestHeader }

import scala.concurrent.{ ExecutionContext, Future }
import utils.auth.PasswordProviderProject._

class PasswordProviderProject @Inject() (
  protected val authInfoRepository: AuthInfoRepository,
  protected val passwordHasherRegistry: PasswordHasherRegistry)(implicit val executionContext: ExecutionContext)
  extends RequestProvider with PasswordProvider with Logger {

  override def id: String = ID

  override def authenticate[B](request: Request[B]): Future[Option[LoginInfo]] = {
    getCredentials(request) match {
      case Some(credentials) =>
        val loginInfo = LoginInfo(id, credentials.identifier)
        authenticate1(loginInfo, credentials.password).map {
          case Authenticated => Some(loginInfo)
          case InvalidPassword(error) =>
            logger.debug(error)
            None
          case UnsupportedHasher(error) => throw new ConfigurationException(error)
          case NotFound(error) =>
            logger.debug(error)
            None
        }
      case None => Future.successful(None)
    }
  }

  def authenticate1(loginInfo: LoginInfo, password: String): Future[State] = {
    authInfoRepository.find[PasswordInfo](loginInfo).flatMap {
      case Some(passwordInfo) => passwordHasherRegistry.find(passwordInfo) match {
        case Some(hasher) if hasher.matches(passwordInfo, password) =>
          if (passwordHasherRegistry.isDeprecated(hasher) || hasher.isDeprecated(passwordInfo).contains(true)) {
            authInfoRepository.update(loginInfo, passwordHasherRegistry.current.hash(password)).map { _ =>
              Authenticated
            }
          } else {
            Future.successful(Authenticated)
          }
        case Some(hasher) => Future.successful(InvalidPassword(PasswordDoesNotMatch.format(id)))
        case None => Future.successful(UnsupportedHasher(HasherIsNotRegistered.format(
          id, passwordInfo.hasher, passwordHasherRegistry.all.map(_.id).mkString(", ")
        )))
      }
      case None => Future.successful(NotFound(PasswordInfoNotFound.format(id, loginInfo)))
    }
  }

  /**
   * Encodes the credentials.
   *
   * @param request Contains the colon-separated name-value pairs in clear-text string format
   * @return The users credentials as plaintext
   */
  def getCredentials(request: RequestHeader): Option[Credentials] = {
    request.headers.get(HeaderNames.AUTHORIZATION) match {
      case Some(header) if header.startsWith("Basic ") =>
        Base64.decode(header.replace("Basic ", "")).split(":", 2) match {
          case credentials if credentials.length == 2 => Some(Credentials(credentials(0), credentials(1)))
          case _ => None
        }
      case _ => None
    }
  }
}

/**
 * The companion object.
 */
object PasswordProviderProject {

  /**
   * The provider constants.
   */
  val ID = "basic-auth"
}
