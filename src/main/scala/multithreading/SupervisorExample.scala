package multithreading

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{OneForOneStrategy, ActorSystem, Props, Actor}
import multithreading.HierarchyExample.{PrintSignal, SignalChildren}

/**
  * Created by jcohe_000 on 3/27/2016.
  */
object SupervisorExample extends App {

  case object CreateChild
  case class SignalChildren(order: Int)
  case class PrintSignal(order: Int)
  case class DivideNumbers(n: Int, d: Int)
  case object BadStuff

  class ParentActor extends Actor {
    private var number = 0

    def receive = {
      case CreateChild =>
        context.actorOf(Props[ChildActor], "child" + number)
        number += 1
      case SignalChildren(n) =>
        context.children.foreach( _ ! PrintSignal(n) )
    }

    override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
      case ae: ArithmeticException => Resume
      case _: Exception => Restart
    }
  }

  class ChildActor extends Actor {
    println("Child created.")
    def receive = {
      case PrintSignal(n) => println(n + " " + self )
      case DivideNumbers(n, d) => println(n/d)
      case BadStuff => throw new RuntimeException("Stuff happened")
    }
    override def preStart() = {
      // Acquire resources.
      super.preStart()
      println("preStart")
    }
    override def postStop() = {
      // Release resources.
      super.postStop()
      println("postStop")
    }

    override def preRestart(reason: Throwable, message: Option[Any]) = {
      super.preRestart(reason, message)
      println("PreRestart")
    }

    override def postRestart(reason: Throwable) = {
      super.postRestart(reason)
      println("PostRestart")
    }

  }


  val system = ActorSystem("hierarch-system")

  val actor = system.actorOf(Props[ParentActor], "parent1")
  val actor2 = system.actorOf(Props[ParentActor], "parent2")

  actor ! CreateChild
  actor ! CreateChild

  val child0 = system.actorSelection("/user/parent1/child0")
  child0 ! DivideNumbers(4, 0)
  child0 ! DivideNumbers(4, 2)
  child0 ! BadStuff


  Thread.sleep(1000)
  system.terminate()
}
