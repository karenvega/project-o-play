package infrastructure.cassandra

import java.util.UUID

import app.Record

case class UserRecord(
  userId: UUID,
  firstName: String,
  lastName: String,
  email: String,
  phone: String,
  userName: String
) extends Record

case class LoginRecord(
  userId: UUID,
  userName: String,
  password: String,
  email: String
) extends Record
