package actor

import akka.actor.{Actor, ActorSelection, Props}
import akka.event.Logging
import com.mailjet.protobuf.messages.{Ack, Send}

object FilterServerActor {
  def props(remoteScheduler: String): Props = Props(new FilterServerActor(remoteScheduler))
}

class FilterServerActor(remoteScheduler: String) extends Actor {
  val log = Logging(context.system, this)

  log.info("sending remotely")
  val actorSelection: ActorSelection = context.actorSelection(s"$remoteScheduler/user/scheduler")
  actorSelection ! Ack("asd")

  def receive: Receive = {
    case Send(payload) => log.info(s"Received payload: $payload")
    case _             => log.info("received unknown message")
  }
}
