package actor

import akka.actor.{Actor, ActorPath, ActorRef, Props, Terminated}
import akka.event.Logging
import com.mailjet.protobuf.messages.{Ack, Exec, Send}

object SchedulerActor {
  def props(failingActor: ActorRef): Props = Props(new SchedulerActor(failingActor))
}

class SchedulerActor(failingActor: ActorRef) extends Actor {
  val log = Logging(context.system, this)
  var filterServers: Map[ActorPath, ActorRef] = Map()

  def receive: Receive = {
    case Ack(id) =>
      log.info(s"received $id from: $sender()")
      context.watch(sender())
      filterServers = filterServers + (sender().path -> sender())
    case Terminated(actorRef) => log.info(s"Terminated -> $actorRef")
    case Exec                 => filterServers.values.foreach(actorRef => actorRef ! Send("something important - an email meta"))
    case "test" => log.info("asd+++++" + failingActor); failingActor ! "sth"
    case _                    => log.info("received unknown message")
  }
}
