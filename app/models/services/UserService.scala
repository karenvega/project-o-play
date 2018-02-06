package models.services

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.{ Credentials, PasswordInfo }
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.{ AuthToken, User }

import scala.concurrent.Future

/**
 * Handles actions to users.
 */
trait UserService extends IdentityService[User] {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param email The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieve(email: String): Future[Option[User]]

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param credentials The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieveSession(credentials: Credentials): Future[LoginInfo]

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]

  def savePassword(login: LoginInfo, aut: PasswordInfo, aut1: AuthToken): Future[PasswordInfo]

  def getPassword(email: String): Future[Option[PasswordInfo]]
}
