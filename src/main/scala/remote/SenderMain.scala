package remote

import akka.actor.{Actor, ActorSystem, Props, Timers}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationDouble

class Sender extends Actor with Timers {
  case object Key
  case object Msg

  override def receive: Receive = {
    case _: String => timers.startPeriodicTimer(Key, Msg, 1.second)
    case Msg => context.actorSelection("akka.tcp://actorSystem2@127.0.0.1:2553/user/receiver") ! "remoteMsg"
  }
}

object SenderMain extends App {
  implicit val system: ActorSystem = ActorSystem("actorSystem1", ConfigFactory.load().getConfig("actorSystem1"))

  system.actorOf(Props[Sender], "sender")

//  system.actorSelection("akka.tcp://actorSystem2@127.0.0.1:2553/user/receiver") ! "remoteMsg"
}
