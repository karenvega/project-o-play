package infrastructure.cassandra

import com.datastax.driver.core.SocketOptions
import com.outworkers.phantom.builder.serializers.KeySpaceSerializer
import com.outworkers.phantom.connectors.{ CassandraConnection, ContactPoint, ContactPoints }
import com.outworkers.phantom.dsl.{ NetworkTopologyStrategy, SimpleStrategy, durable_writes, replication }
import infrastructure.config.DefaultConfig

import scala.language.reflectiveCalls

object Connector {

  private val cassandraConf = DefaultConfig.cassandraConfig

  val strategy = cassandraConf.strategy.toUpperCase match {
    case "NETWORKTOPOLOGYSTRATEGY" =>
      NetworkTopologyStrategy.option("replication_factor", cassandraConf.replicationFactor)
    case _ => SimpleStrategy.option("replication_factor", cassandraConf.replicationFactor)
  }

  lazy val connector: CassandraConnection = {
    ContactPoints(Seq(cassandraConf.host))
      .withClusterBuilder(
        _.withCredentials(cassandraConf.username, cassandraConf.password)
          .withPort(cassandraConf.port)
          .withSocketOptions(new SocketOptions().setConnectTimeoutMillis(cassandraConf.connectionTimeout))
      )
      .noHeartbeat()
      .keySpace(
        cassandraConf.keyspace,
        autoinit = cassandraConf.createKeyspace,
        query = Option(
          KeySpaceSerializer(cassandraConf.keyspace)
            .ifNotExists()
            .`with`(replication eqs strategy)
            .and(durable_writes eqs true)
        )
      )
  }

  lazy val testConnector: CassandraConnection = ContactPoint.embedded.noHeartbeat().keySpace(cassandraConf.keyspace)
}
