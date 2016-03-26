package push

import akka.actor.{Props, Actor}

case object MyMessage
case object AnotherMessage

class MyActor extends Actor {
  val anotherActor = createAnother
  def receive: Receive = {
    case MyMessage => anotherActor ! AnotherMessage
  }

  def createAnother = context.actorOf(Props[AnotherActor])
}

class AnotherActor extends Actor{
  def receive = {
    case _ =>
  }
}