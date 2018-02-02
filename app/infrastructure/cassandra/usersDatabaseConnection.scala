package infrastructure.cassandra

import infrastructure.cassandra.Connector.{ connector, testConnector }

trait UserDatabaseProvider {

  def database: UsersDatabase
}

object UsersProductionDb extends UsersDatabase(connector)

trait ProductionDatabase extends UserDatabaseProvider {

  val database = UsersProductionDb
}

object UsersEmbeddedDb extends UsersDatabase(testConnector)

trait EmbeddedDatabase extends UserDatabaseProvider {

  val database = UsersEmbeddedDb
}
