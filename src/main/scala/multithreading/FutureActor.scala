package multithreading

import java.util.concurrent.Executors

import akka.actor.Actor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import scala.concurrent.ExecutionContext.Implicits.global

class FutureActor extends Actor {

  implicit val ec = Executors.newFixedThreadPool(4)

  def receive = {
    case _ =>
  }

  // Spawn your futures
  val fs = (1 to 100).map { i =>
    Future { Thread.sleep(i); i }
  }

  // Wrap all of the work up into a single
  // Future
  val f = Future.sequence(fs)

  // Wait on it forever - i.e. until it's done
  Await.result(f, Duration.Inf)

  // Shut down
  // system.terminate()
}
