package infrastructure.config

trait ConfigApp {
  def cassandraConfig: CassandraConfig
  def httpConfig: HttpConfig
}
