package parentchild

import akka.actor.{Actor, ActorLogging}
import akka.event.Logging
import parentchild.Child.ForceRestart


object Child {
  case object ForceRestart
}

class Child extends Actor with ActorLogging {
  override val log = Logging(context.system, this)
  
  var state = 0
  def receive = {
    case ForceRestart => throw new Exception("Boom!")
    case ex: Exception => throw ex
    case x: Int        => state = x
    case "get"         => sender() ! state
  }

  log.info("entered the Child constructor")

  override def preStart { log.info("Child: preStart") }
  override def postStop { log.info("Child: postStop") }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.info("Child: preRestart")
    log.info(s"  MESSAGE: ${message.getOrElse("")}")
    log.info(s"  REASON: ${reason.getMessage}")
    super.preRestart(reason, message)
  }
  override def postRestart(reason: Throwable) {
    log.info("Child: postRestart")
    log.info(s"  REASON: ${reason.getMessage}")
    super.postRestart(reason)
  }
}