package actor

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props, Terminated, Timers}

object TerminatorTest extends App{
  implicit val actorSystem: ActorSystem = ActorSystem("ASTerm")

  val parent = actorSystem.actorOf(Props[Parent], "parent")


//  actorSystem.stop(parent)
//  parent ! PoisonPill
  parent ! "send it"
//  parent ! "kill it"
}

class Parent extends Actor with ActorLogging with Timers {
  import ShutDownHelper._

  val child: ActorRef = context.actorOf(Props[Child], "child")
  context.watch(child)
  (1 to 50).foreach(n => child ! n)

  override def receive: Receive = {
    case "send it" => log.info("Send poison pill"); child ! ShutDown
    case "kill it" => context.stop(child)
    case Terminated(actorRef) => log.info(s"received terminated $actorRef")
  }
}

case object CustomPoisonPill

class Child extends Actor with ActorLogging with Timers {
  import ShutDownHelper._

  log.info("child in your athmosphere")
  val grandchild: ActorRef = context.actorOf(Props[GrandChild], "grandchild")
  context.watch(grandchild)
  (1 to 100).foreach(n => grandchild ! n)

  override def preStart(): Unit = log.info("child In preStart")

  override def postStop(): Unit = log.info("child In postStop")

  override def receive: Receive = {
    case n: Int => log.info(s"in C $n")
    case "msg" => throw new Exception("Olele")
    case ShutDown =>
      context.become(inShutdownBehaviour(Set(grandchild), log, timers))
  }
}

class GrandChild extends Actor with ActorLogging {
  import ShutDownHelper._

  log.info("grandchild in your athmosphere")

  override def preStart(): Unit = log.info("grandchild In preStart")

  override def postStop(): Unit = log.info("granchild In postStop")

  override def receive: Receive = {
    case n: Int => log.info(s"in GC $n")
    case "msg" => throw new Exception("Olele")
    case ShutDown => context.stop(self)
  }
}