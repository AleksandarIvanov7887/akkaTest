import AkidActor.Pull
import SchedulerActor.{Hello, Send}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.{BroadcastRoutingLogic, Router}

object AkidActor {
  def props(scheduler: ActorRef): Props = Props(new AkidActor(scheduler))
  case object Pull
}

class AkidActor(scheduler: ActorRef) extends Actor with ActorLogging {
  override def preStart(): Unit = scheduler ! Hello

  override def receive: Receive = {
    case Pull => log.info("received pull")
  }
}

object SchedulerActor {
  def props(): Props = Props(new SchedulerActor())
  case object Hello
  case object Send
}

class SchedulerActor extends Actor with ActorLogging {
  var router = Router(BroadcastRoutingLogic())

  override def receive: Receive = {
    case Hello  =>
      log.info("adding")
      context.watch(sender())
      router = router.addRoutee(sender())
    case Send   =>
      log.info(router.routees.toString())
      log.info("sending pull")
      context.actorSelection("/user/akid-**") ! Pull
//      router.route(Pull, self)
  }
}

object BroadcastTest extends App {
  implicit val system: ActorSystem = ActorSystem("BroadcastTest")

  val scheduler = system.actorOf(SchedulerActor.props(), "scheduler")

//  (1 to 10).foreach(number => system.actorOf(AkidActor.props(scheduler), s"akid-$number"))

  Thread.sleep(5000)
  scheduler ! Send
}
