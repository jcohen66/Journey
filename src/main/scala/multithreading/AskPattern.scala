package multithreading

import akka.actor.{Props, ActorSystem, Actor}
import akka.util.Timeout
import multithreading.AskPattern.AskName
import akka.pattern._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by jcohe_000 on 3/27/2016.
  */
object AskPattern extends App {

  case object AskName
  case class NameResponse(name: String)

  class AskActor(val name: String) extends Actor {

    def receive = {
      case AskName => sender ! NameResponse(name)
    }
  }

  val system = ActorSystem("SimpleSystem")
  val actor = system.actorOf(Props(new AskActor("Pat")), "AskActor")

  implicit val timeout = Timeout(1.seconds)
  val answer = actor ? AskName

  answer.foreach(n => println("Name is: " + n))

  system.terminate()
}
