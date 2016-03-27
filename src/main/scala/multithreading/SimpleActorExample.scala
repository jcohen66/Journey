package multithreading

import akka.actor.{Props, ActorSystem, Actor}

/**
  * Created by jcohe_000 on 3/27/2016.
  */
object SimpleActorExample extends App {
  class SimpleActor extends Actor {

    def receive = {
      case s: String => println("String: " + s)
      case i: Int => println("Number: " + i)
    }

  }

  def foo = println("Normal method")

  val system = ActorSystem("simple-system")
  val actor = system.actorOf(Props[SimpleActor], "simple-actor")

  println("Before messages")

  actor ! "Hi there"

  println("After string")

  actor ! 42

  println("After int")

  actor ! 'a'

  println("After char")

  system.terminate()
}
