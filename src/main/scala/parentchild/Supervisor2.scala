package parentchild

import akka.actor.{ActorLogging, Actor, Props}
import akka.event.Logging

class Supervisor2 extends Actor with ActorLogging {
  override val log = Logging(context.system, this)

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException      =>
        log.info("Supervisor: AE trapped - Resuming")
        Resume
      case _: NullPointerException     =>
        log.info("Supervisor: NPE trapped - Restarting")
        Restart
      case _: IllegalArgumentException =>
        log.info("Supervisor: IAE trapped - Stopping")
        Stop
      case _: Exception                =>
        log.info("Supervisor: Some other exception trapped - Escalating")
        Escalate
    }

  def receive = {
    case p: Props => sender() ! context.actorOf(p)
  }
  // override default to kill all children during restart
  override def preRestart(cause: Throwable, msg: Option[Any]) {}
}