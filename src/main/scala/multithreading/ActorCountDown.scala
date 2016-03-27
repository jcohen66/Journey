package multithreading

import akka.actor.{ActorRef, Props, ActorSystem, Actor}

/**
  * Created by jcohe_000 on 3/27/2016.
  */

  object ActorCountDown extends App {

    case class StartCounting(n: Int, other: ActorRef)
    case class CountDown(n: Int)

    class CountDownActor extends Actor {

      def receive = {
        case StartCounting(n, other) =>
          println(n)
          other ! CountDown(n-1)
        case CountDown(n) =>
          println(self)           // self refers to actorRef of the actor.
          if(n > 0) {
            println(n)
            sender ! CountDown(n - 1)
          } else {
            context.system.terminate()
          }

      }

    }


    val system = ActorSystem("countdown-system")
    val actor1 = system.actorOf(Props[CountDownActor], "CountDown1")
    val actor2 = system.actorOf(Props[CountDownActor], "CountDown2")

    actor1 ! StartCounting(10, actor2)
}
