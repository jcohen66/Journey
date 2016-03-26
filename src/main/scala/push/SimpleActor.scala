package push

import akka.actor.{Cancellable, Actor, ActorRef}
import scala.concurrent.duration._
import scala.collection.script.Start

/**
  * Created by jcohe_000 on 3/24/2016.
  */
object SimpleActor {
  case class DataToHandle(bytes: Array[Byte])
  case object GetData
}

class SimpleActor(implicit otherActor: Option[ActorRef]) extends Actor {
  import SimpleActor._
  import context.dispatcher

  var cancellable: Option[Cancellable] = None
  def receive = {
    case GetData =>
      println("Got a GetData message!")
      context.become(dataHandler)
      otherActor match {
        case Some(o) => cancellable = Some(context.system.scheduler.schedule(0 milliseconds, 500 millisecond, o, GetData))
        case None => // Do nothing
      }

  }

  def dataHandler: Receive = {
    case DataToHandle(data) =>
      // Do something
    cancellable map {_.cancel}
      context.unbecome
  }

}