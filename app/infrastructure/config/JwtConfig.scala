package infrastructure.config

case class TestMode(enabled: Boolean, issValue: String)

case class JwtConfig(key: String, algorithm: String, testMode: TestMode)
