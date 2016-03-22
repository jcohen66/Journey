package monitoring

/**
  * Created by jcohe_000 on 3/21/2016.
  */

import akka.actor.{ActorRef, ActorSystem, Props, Actor, Terminated}


class Ares(athena: ActorRef) extends Actor {

  override def preStart() = {
    context.watch(athena)
  }

  override def postStop() = {
    println("Ares postStop...")
  }

  def receive = {
    case Terminated =>
      println("Ares stopping itself...")
      context.stop(self)
  }
}

class Athena extends Actor {

  override def postStop() = {
    println("Athena postStop...")
  }

  def receive = {
    case msg =>
      println(s"Athena received ${msg}")
      context.stop(self)
  }

}

object Monitoring extends App {

  // Create the 'monitoring' actor system
  val system = ActorSystem("monitoring")

  val athena = system.actorOf(Props[Athena], "athena")

  val ares = system.actorOf(Props(classOf[Ares], athena), "ares")

  athena ! "Hi"

  system.terminate()


}