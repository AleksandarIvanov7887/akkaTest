package actor

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Timers}

class TimingActor extends Actor with ActorLogging with Timers {
  override def receive: Receive = {
    case "start" => log.info("start"); timers.startSingleTimer(sender.path.address, "check", 10.seconds)
    case "stop"  => log.info("stop"); //timers.cancel(sender.path.address)
    case "check" => log.info("Check")
  }
}

case object Key
case object Msg

class TimingActor2 extends Actor with ActorLogging with Timers {
  timers.startSingleTimer(Key, Msg, 5.seconds)

  override def receive: Receive = {
    case Msg => log.info("Yess, 2")
  }
}

class TimingActor3 extends Actor with ActorLogging with Timers {
  timers.startSingleTimer(Key, Msg, 10.seconds)

  override def receive: Receive = {
    case Msg => log.info("Yess, 3")
  }
}

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("TestTIME")

//  val ta = system.actorOf(Props[TimingActor], "timer")
//
//  ta ! "start"
//  ta ! "stop"

  val ta2 = system.actorOf(Props[TimingActor2], "timer2")
  val ta3 = system.actorOf(Props[TimingActor3], "timer3")
}