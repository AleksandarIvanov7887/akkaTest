import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.io.StdIn

object ConfMain extends App {
  val config1 = ConfigFactory.load()

  println("complex-app.something=" + config1.getString("complex-app.something"))
//  println("from static" + config1.getString("tt.path"))

  ConfigFactory.invalidateCaches()
  StdIn.readLine()

  val config2 = ConfigFactory.load()

  println("complex-app.something=" + config2.getString("complex-app.something"))
//  println("from static" + config1.getString("tt.path"))
}
