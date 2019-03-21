import akka.http.scaladsl.model.Uri
import io.aventus.service.akka.BaseConfig
import io.aventus.service.akka.BaseConfig.{JwtConfig, SwaggerConfig}
import pureconfig._
import pureconfig.ConvertHelpers._

import scala.language.postfixOps

case class AppConfig(
                      requireHttps: Boolean,
                      httpPort: Int,
                      jwt: JwtConfig,
                    ) extends BaseConfig {

  override val serviceName = "prudence"

  override val swagger = SwaggerConfig()
}

object AppConfig {
  private implicit val uriReader: ConfigReader[Uri] = ConfigReader.fromString[Uri](catchReadError(Uri(_)))
  private val config: AppConfig = loadConfig[AppConfig].fold(err => throw new Exception(s"Error loading configuration: $err"), identity)

  def apply(): AppConfig = config
}
