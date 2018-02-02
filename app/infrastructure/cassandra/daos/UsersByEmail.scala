package infrastructure.cassandra.daos

import com.outworkers.phantom.dsl.{ ClusteringOrder, ConsistencyLevel, PartitionKey, ResultSet, RootConnector, Row, Table, _ }
import infrastructure.cassandra.LoginRecord

import scala.concurrent.Future

abstract class UsersByEmail extends Table[UsersByEmail, LoginRecord] {

  override def tableName: String = "users_by_login"

  object UserID extends TimeUUIDColumn {
    override lazy val name = "user_id"
  }

  object UserName extends StringColumn {
    override lazy val name = "user_name"
  }

  object Password extends StringColumn with ClusteringOrder {
    override lazy val name = "password"
  }

  object Email extends StringColumn with PartitionKey {
    override lazy val name = "email"
  }

  override def fromRow(r: Row): LoginRecord =
    LoginRecord(UserID(r), UserName(r), Password(r), Email(r))
}

abstract class UsersByEmailDAO extends UsersByEmail with RootConnector {

  def getByUserEmail(email: String, password: String): Future[Option[LoginRecord]] =
    select
      .where(_.Email eqs email)
      .and(_.Password eqs password)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()

  def store(login: LoginRecord): Future[ResultSet] =
    insert
      .value(_.UserID, login.userId)
      .value(_.UserName, login.userName)
      .value(_.Password, login.password)
      .value(_.Email, login.email)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()

  def deleteByLogin(username: String): Future[ResultSet] =
    delete
      .where(_.Email eqs username)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
}
