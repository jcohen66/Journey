package multithreading

import akka.actor.{ActorRef, Props, ActorSystem, Actor}

/**
  * Created by jcohe_000 on 3/27/2016.
  */
object HierarchyExample extends App {

  case object CreateChild
  case object SignalChildren
  case object PrintSignal

  class ParentActor extends Actor {
    private var number = 0
    private val children = collection.mutable.Buffer[ActorRef]()

    def receive = {
      case CreateChild =>
        context.actorOf(Props[ChildActor], "child" + number)
        number += 1
      case SignalChildren =>
        children.foreach( _ ! PrintSignal )
    }
  }

  class ChildActor extends Actor {
    def receive = {
      case PrintSignal => println(self)
      case _ =>
    }
  }


  val system = ActorSystem("hierarch-system")

  val actor = system.actorOf(Props[ParentActor], "parent-actor")

  actor ! CreateChild
  actor ! SignalChildren
  actor ! CreateChild
  actor ! CreateChild
  actor ! SignalChildren

  system.terminate()
}
