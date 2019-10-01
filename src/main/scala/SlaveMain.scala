import actor.FilterServerActor
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor

object SlaveMain extends App {
  val config = ConfigFactory.load()
  implicit val system: ActorSystem = ActorSystem("ActorSystem2", config.getConfig("actorSystem2"))
  implicit val execContext: ExecutionContextExecutor = system.getDispatcher

  system.actorOf(FilterServerActor.props(config.getString("remoteScheduler")), "filterServer1")
  system.actorOf(FilterServerActor.props(config.getString("remoteScheduler")), "filterServer2")
}
