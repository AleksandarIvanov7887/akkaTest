import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble
import scala.io.StdIn
import net.ceedubs.ficus.Ficus._

import scala.collection.concurrent.TrieMap

object RestartMain extends App {
  var system = mainBody()

  StdIn.readLine()
  system = restart(system)

  def restart(system: ActorSystem): ActorSystem = {
    shutdown(system)
    mainBody()
  }


  def shutdown(system: ActorSystem): Unit = {
    Await.result(system.terminate(), 10.seconds)
  }

  def mainBody(): ActorSystem = {
    ConfigFactory.invalidateCaches()
    val config = ConfigFactory.load()
    println(config.getString("akka.http.client.parsing.illegal-header-warnings"))
    implicit val system: ActorSystem = ActorSystem("ForRestart")

    system
  }
}

trait Settings {
  def config (invalidateCache: Boolean = false): Config = {
    if (invalidateCache) { ConfigFactory.invalidateCaches() }
    ConfigFactory.load()
  }
  def engine: EngineSettings
}

trait EngineSettings {
  def weight: String
}

class AppSettings extends Settings {
  val c: Config = config(true)

  override def engine: EngineSettings = new EngineSettings {
    override def weight: String = c.as[String]("akka.http.client.parsing.illegal-header-warnings")
  }
}

object Settings {
  private val namedSettings = new TrieMap[Long, AppSettings]
  def load(): Settings = {
    val loadedUpToDate = new AppSettings
    namedSettings += ((System.currentTimeMillis, loadedUpToDate))
    new Settings {
      override def engine: EngineSettings = loadedUpToDate.engine
    }
  }
}

object Maa extends App {
  println(Settings.load().engine.weight)
  StdIn.readLine()
  println(Settings.load().engine.weight)
}