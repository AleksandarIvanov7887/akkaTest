import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.concurrent.{ExecutionContextExecutor, Future}


class SelfActor(ar: ActorRef) extends Actor {
  case object Msg
  implicit val exec: ExecutionContextExecutor = context.system.getDispatcher

  Future {
    println("Start")
    Thread.sleep(4000)
    println("End")
  }.map {
    _ => ar ! Msg2
  }

  override def receive: Receive = {
    case Msg => println(sender())
  }
}

case object Msg2

class SelfActor2 extends Actor {
  implicit val exec: ExecutionContextExecutor = context.system.getDispatcher

  override def receive: Receive = {
    case Msg2 => println(sender())
  }
}




object SelfTest extends App {
  implicit val system: ActorSystem = ActorSystem("ActorSystem")

  system.actorOf(Props(new SelfActor(system.actorOf(Props[SelfActor2], "receiver"))), "sender")
}
