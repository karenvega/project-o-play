package infrastructure.cassandra

import app.Repository
import models.User

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait UsersRepository extends Repository { self: UserDatabaseProvider =>

  def authenticateUserByUsername(user: String, password: String): Future[Option[LoginRecord]] = {
    database.userByLogin.getByUserByUsername(user, password)
  }

  def authenticateUserByEmail(user: String, password: String): Future[Option[LoginRecord]] = {
    database.userByEmail.getByUserEmail(user, password)
  }

  def saveOrUpdate(user: User): Future[User] =
    for {
      _ <- database.userByLogin.store(loginRecordFromUser(user))
      _ <- database.userByEmail.store(loginRecordFromUser(user))
    } yield user

  def loginRecordFromUser(user: User): LoginRecord = {
    LoginRecord(
      userId = user.userID,
      userName = user.loginInfo.providerID,
      password = user.loginInfo.providerKey,
      email = user.email.getOrElse("")
    )
  }

}