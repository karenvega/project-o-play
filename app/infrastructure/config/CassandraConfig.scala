package infrastructure.config

case class CassandraConfig(
  host: String,
  keyspace: String,
  port: Int,
  username: String,
  password: String,
  createKeyspace: Boolean,
  connectionTimeout: Int,
  strategy: String,
  replicationFactor: Int
)
