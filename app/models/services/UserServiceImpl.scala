package models.services

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.{ Credentials, PasswordInfo }
import models.{ AuthToken, User }
import models.daos.UserDAO
import play.api.db.Database

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 * @param ex      The execution context.
 */
class UserServiceImpl @Inject() (userDAO: UserDAO, db: Database)(implicit ex: ExecutionContext) extends UserService {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param email The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieve(email: String) = {
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * from users where email = '$email' ")
      if (rs.next()) {
        val id = rs.getString("userID")
        val name = rs.getString("firstName")
        val lastName = rs.getString("lastName")
        val fullName = rs.getString("fullName")
        val email = rs.getString("email")
        val activated = rs.getString("activated")
        val providerId = rs.getString("providerId")
        val providerKey = rs.getString("providerKey")
        Future.successful(Some(User(UUID.fromString(id), LoginInfo(providerId, providerKey), Some(name), Some(lastName), Some(fullName), Some(email), None, activated.toBoolean)))
      } else {
        Future.successful(Option.empty)
      }
    } finally {
      conn.close()
    }

  }

  def retrieveSession(credentials: Credentials): Future[LoginInfo] = {
    println("karen" + credentials + "...aaaaaaaaaaaaaa")
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * from users where email = '${credentials.identifier}' and password = '${credentials.password}' ")
      if (rs.next()) {

        val providerId = rs.getString("providerId")
        val providerKey = rs.getString("providerKey")
        Future.successful(LoginInfo(providerId, providerKey))
      } else {
        Future.successful(LoginInfo("credentials", ""))
      }
    } finally {
      conn.close()
    }
  }

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param loginInfo The login info to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given login info.
   */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * from users where providerId = '${loginInfo.providerID}' and providerKey = '${loginInfo.providerKey} '")
      if (rs.next()) {
        val id = rs.getString("userID")
        val name = rs.getString("firstName")
        val lastName = rs.getString("lastName")
        val fullName = rs.getString("fullName")
        val email = rs.getString("email")
        val activated = rs.getString("activated")
        val providerId = rs.getString("providerId")
        val providerKey = rs.getString("providerKey")
        Future.successful(Some(User(UUID.fromString(id), LoginInfo(providerId, providerKey), Some(name), Some(lastName), Some(fullName), Some(email), None, activated.toBoolean)))
      } else {
        Future.successful(Option.empty)
      }
    } finally {
      conn.close()
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    val conn = db.getConnection()
    try {
      conn.prepareStatement(s"INSERT INTO users(userID, firstName, lastName, fullName, email, activated, providerID, providerKey, password) " +
        s"values ( '${user.userID}', '${user.firstName.get}', '${user.lastName.get}', " +
        s"'${user.fullName.get}', '${user.email.get}', 'true', '${user.loginInfo.providerID}', " +
        s"'${user.loginInfo.providerKey}', '${user.password.get}' )").execute()
      Future.successful(user)
    } finally {
      conn.close()
    }
  }

  def savePassword(login: LoginInfo, aut: PasswordInfo, aut1: AuthToken) = {
    val conn = db.getConnection()
    try {
      conn.prepareStatement(s"INSERT INTO Login (email, hasher, password, salt) " +
        s"values ( '${login.providerKey}', '${aut.hasher}', '${aut.password}', '${aut.salt.getOrElse("")}' )").execute()
      Future.successful(aut)
    } finally {
      conn.close()
    }
  }

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param email The login info to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given login info.
   */
  def getPassword(email: String): Future[Option[PasswordInfo]] = {
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * from Login where email = '${email} '")
      if (rs.next()) {
        val email = rs.getString("email")
        val hasher = rs.getString("hasher")
        val password = rs.getString("password")
        val salt = rs.getString("salt")
        Future.successful(Some(PasswordInfo(hasher, password, Some(salt))))
      } else {
        Future.successful(Option.empty)
      }
    } finally {
      conn.close()
    }
  }
}
