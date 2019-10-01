package remote

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Timers}
import akka.remote.{AssociatedEvent, DisassociatedEvent, RemotingLifecycleEvent, RemotingListenEvent, RemotingShutdownEvent, ThisActorSystemQuarantinedEvent}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration.DurationDouble

class Receiver extends Actor with Timers {
  context.system.eventStream.subscribe(self, classOf[DisassociatedEvent])

  context.system.actorSelection("akka.tcp://actorSystem1@127.0.0.1:2552/user/sender") ! "start"

  override def receive: Receive = {
//    case _: AssociatedEvent => println("assoc")
    case DisassociatedEvent(localAddress, remoteAddress, inbound) =>
      println("DISASS")
      println(localAddress)
      println(remoteAddress)
      println(inbound)
//      timers.startPeriodicTimer("key", "ping", 5.seconds)
//    case _: RemotingListenEvent => println("ready to get back")
//    case _: ThisActorSystemQuarantinedEvent => println("quarantine")
//    case RemotingShutdownEvent => println("remote shut down")
//    case "remoteMsg" => println("received remoteMsg")
//    case "ping" => context.system.actorSelection("akka.tcp://actorSystem1@127.0.0.1:2552/user/sender") ! "start"
  }
}

object ReceiverMain extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("actorSystem2", ConfigFactory.load().getConfig("actorSystem2"))

  actorSystem.actorOf(Props[Receiver], "receiver")

}
