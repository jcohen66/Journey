package router

import akka.actor.{ActorSystem, Props}
import akka.routing.{FromConfig, RoundRobinPool}
import router.Worker._

object RoundRobin extends App {

  val system = ActorSystem("Round-Robin-Router")

  val routerPool = system.actorOf(RoundRobinPool(3).props(Props[Worker]), "round-robin-pool")

  routerPool ! Work()

  routerPool ! Work()

  routerPool ! Work()

  routerPool ! Work()

  Thread.sleep(100)

  system.actorOf(Props[Worker], "w1")
  system.actorOf(Props[Worker], "w2")
  system.actorOf(Props[Worker], "w3")


  val routerGroup = system.actorOf(FromConfig.props(), "round-robin-group")

  routerGroup ! Work()

  routerGroup ! Work()

  routerGroup ! Work()

  routerGroup ! Work()

  Thread.sleep(100)

  system.shutdown()
}