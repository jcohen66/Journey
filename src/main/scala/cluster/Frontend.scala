package cluster

/**
  * Created by jcohe_000 on 3/21/2016.
  */

import akka.cluster._
import commons._
import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}


class Frontend extends Actor {

  var backends = IndexedSeq.empty[ActorRef]

  println("*** " + backends.size + " ****")

  def receive = {
    case Add if backends.isEmpty =>
      println("Service unavailable, cluster doesn't have backend node.")
    case addOp: Add =>
      println("Frontend: I'll forward add operation to backend node to handle it.")
      val x = if (backends.size > 0) backends.size else 1

      backends(Random.nextInt(x)) forward addOp
    case BackendRegistration if !(backends.contains(sender())) =>
      backends = backends :+ sender()
      context watch (sender())
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
    case Ping(num) => println(s"Frontend Rec'd Ping: ${num} from Backend")
      backends(Random.nextInt(backends.size)) forward Pong(num)
    case Pong(num) => println(s"Frontend Rec'd Pong: ${num} from Backend")
      backends(Random.nextInt(backends.size)) forward Ping(num)
  }

}


object Frontend {

  private var _frontend: ActorRef = _
  private var _system: Option[ActorSystem] = None

  def system = _system;

  def initiate() = {

    withConfig {
      config => {
        _system = Some(ActorSystem("ClusterSystem", config))

        withActorSystem {
          sys: ActorSystem => {
            _frontend = sys.actorOf(Props[Frontend], name = "frontend")
          }
        }
      }


    }
    _frontend
  }

  def getFrontend = _frontend


  /**
    * Define a fail-safe wrapper for ActorSystem that throws
    * a IllegalStateException in the event that the actorsystem
    * is not available.
    *
    * @param block
    * @tparam A
    * @return
    */
  def withActorSystem[A](block: ActorSystem => A): A = {
    _system match {
      case Some(s) => block(s)
      case None => throw new IllegalStateException("Actor System not initialized.")
    }
  }


  /**
    * Define a fail-safe wrapper for Config that throws
    * a IllegalStateException in the event that the config
    * is not readable from the file.
    *
    * @param f
    * @tparam A
    * @return
    */
  def withConfig[A](f: Config => A): A = {
    val config = ConfigFactory.load().getConfig("Frontend")

    config.isEmpty match {
      case true => throw new IllegalStateException("Config for Frontend not found")
      case false => f(config)
    }
  }

}