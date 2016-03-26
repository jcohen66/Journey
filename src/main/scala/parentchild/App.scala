package parentchild

import akka.actor.{ActorSystem, Props}

/**
  * Created by jcohen66 on 3/26/16.
  */
object Main extends App {
  val system = ActorSystem("parent-child")
  val supervisor2 = system.actorOf(Props[Supervisor2], "supervisor-3")

  supervisor2 ! Props[Child]
  val child3 = system.actorOf(Props[Child])

  child3 ! 23
  child3 ! "get"

}
