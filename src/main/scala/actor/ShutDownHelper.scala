package actor

import akka.actor.Actor.Receive
import akka.actor.{ActorContext, ActorRef, Terminated, TimerScheduler}
import akka.event.LoggingAdapter
import scala.concurrent.duration.DurationDouble

object ShutDownHelper {
  case object ShutDown
  case object ShutDownTimerKey
  case object ShutDownTimeExceeded

  def inShutdownBehaviour(actorRefs: Set[ActorRef], log: LoggingAdapter, timers: TimerScheduler)(implicit context: ActorContext): Receive = {
    actorRefs.foreach(_ ! ShutDown)
    timers.startSingleTimer(ShutDownTimerKey, ShutDownTimeExceeded, 10.seconds)
    inShutdownBehaviour(actorRefs, log)
  }

  private def inShutdownBehaviour(actorRefs: Set[ActorRef], log: LoggingAdapter)(implicit context: ActorContext): Receive = {
    case Terminated(actorRef) =>
      log.info(s"Terminated actor - $actorRef")
      val remaining = actorRefs - actorRef
      if (remaining.isEmpty) {
        log.info(s"All watched actors terminated")
        context.stop(context.self)
      }
      context.become(inShutdownBehaviour(remaining, log))
    case ShutDownTimeExceeded =>
      context.stop(context.self)
  }
}