package actor

import akka.actor.{Actor, ActorLogging}

class FailingActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case "sth" => log.info(self.path.toString); throw new Exception("problem")
  }

  override def preStart(): Unit = {
    log.info("---> prestart")
  }

  override def postStop(): Unit = {
    log.info("---> poststop")
  }
}
