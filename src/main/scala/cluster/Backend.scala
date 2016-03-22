package cluster

/**
  * Created by jcohe_000 on 3/21/2016.
  */

import akka.cluster._
import com.typesafe.config.{Config, ConfigFactory}
import commons.{Pong, Ping, BackendRegistration, Add}

import akka.cluster.ClusterEvent.MemberUp
import akka.actor.{Actor, ActorRef, ActorSystem, Props, RootActorPath}

class Backend extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  def receive = {
    case Add(num1, num2) =>
      println(s"I'm a backend with path: ${self} and I received add operation.")
    case MemberUp(member) =>
      if (member.hasRole("frontend")) {
        context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
          BackendRegistration
      }
    case Ping(num) => println(s"Backend Rec'd Ping: ${num} from Frontend")
      sender() ! Pong(num)
    case Pong(num) => println(s"Backend Rec'd Pong: ${num} from Frontend")
      sender() ! Ping(num)
  }

}

object Backend {
  private var _system: Option[ActorSystem] = None

  def system = _system;

  def initiate(port: Int) = {

    withConfig(port) {
      config => {
        _system = Some(ActorSystem("ClusterSystem", config))

        withActorSystem {
          sys: ActorSystem => {
            val Backend = sys.actorOf(Props[Backend], name = "Backend")
          }
        }
      }

    }
//    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
//      withFallback(ConfigFactory.load().getConfig("Backend"))




  }

  /**
    * Define a fail-safe wrapper for ActorSystem that throws
    * a IllegalStateException in the event that the actorsystem
    * is not available.
    *
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
    * @param port
    * @param f
    * @tparam A
    * @return
    */
  def withConfig[A](port: Int)(f: Config => A): A = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.load().getConfig("Backend"))

    config.isEmpty match {
      case true => throw new IllegalStateException("Config for Backend not found")
      case false => f(config)
    }
  }

}