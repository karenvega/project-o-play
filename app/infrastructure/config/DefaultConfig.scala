package infrastructure.config

import scala.collection.immutable.Seq
import cats.data.NonEmptyList
import cats.implicits.{ catsSyntaxEither, catsSyntaxUCartesian }
import pureconfig.error._
import pureconfig.loadConfig

object DefaultConfig extends ConfigApp {

  private val configValidations = (
    loadConfig[CassandraConfig]("owasp-training.cassandra").toValidatedNel |@|
    loadConfig[HttpConfig]("owasp-training.http").toValidatedNel |@|
    loadConfig[JwtConfig]("owasp-training.jwt").toValidatedNel
  ).tupled

  val (
    cassandraConfig,
    httpConfig,
    jwtConfig
    ) =
    configValidations.fold(throwException, identity)

  private[infrastructure] def throwException(failures: NonEmptyList[ConfigReaderFailures]): Nothing = {
    val msg: String = buildMessage(failures)
    throw new Exception(msg)
  }

  private[infrastructure] def buildMessage(nestedFailures: NonEmptyList[ConfigReaderFailures]): String = {
    val flatFailures: Seq[ConfigReaderFailure] = nestedFailures.map(_.toList).toList.flatten

    val errors: String = flatFailures.map {
      case CannotParse(msg, _) => s"Can't not parse: $msg"
      case CannotConvertNull(_) => "Found null config"
      case CannotConvert(value, toTyp, because, _, _) => s"Att $value can't be set to $toTyp because $because"
      case CollidingKeys(key, existingValue, _) => s"Att error: CollidingKeys $key - $existingValue"
      case KeyNotFound(key, _, _) => s"Att $key not found"
      case UnknownKey(key, _) => s"Att $key unknown"
      case WrongType(foundTyp, expectedTyp, _, _) => s"Att has wrong type: found $foundTyp, expected $expectedTyp"
      case ThrowableFailure(throwable, _, _) => s"Config throwable was thrown ${throwable.getMessage}"
      case EmptyStringFound(typ, _, _) => s"Att empty string found $typ"
      case NoValidCoproductChoiceFound(value, _, _) => s"Att No valid coproduct $value"
    } mkString ", "

    s"Errors while parsing config file: [$errors]"
  }
}
