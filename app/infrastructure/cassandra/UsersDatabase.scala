package infrastructure.cassandra

import com.outworkers.phantom.dsl.{ Database, KeySpaceDef }
import infrastructure.cassandra.daos.{ UsersByEmailDAO, UsersByLoginDAO }

class UsersDatabase(val keyspace: KeySpaceDef) extends Database[UsersDatabase](keyspace) {

  object userByLogin extends UsersByLoginDAO with keyspace.Connector
  object userByEmail extends UsersByEmailDAO with keyspace.Connector

}
