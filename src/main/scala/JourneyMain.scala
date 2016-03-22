import akka.actor.ActorSystem
import cluster.{Backend, Frontend}
import commons.{Ping, Add}


/**
  * Created by jcohe_000 on 3/21/2016.
  */
object JourneyMain extends App {

  Frontend.initiate()

  //initiate three nodes from backend
  Backend.initiate(2552)

  Backend.initiate(2560)

  Backend.initiate(2561)

  Thread.sleep(10000)

  Frontend.getFrontend ! Add(2, 4)

  Thread.sleep(2000)

  Frontend.getFrontend ! Ping(244)

}
