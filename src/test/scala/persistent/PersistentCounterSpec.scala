package persistent

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{MustMatchers, BeforeAndAfterAll, FlatSpecLike}
import persistent.Counter._

/**
  * Created by jcohe_000 on 3/21/2016.
  */
class PersistentCounterSpec extends TestKit(ActorSystem("test-system"))
  with ImplicitSender
  with FlatSpecLike
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  "Counter" should "have zero value if Evt(Zero(0)) is sent" in {

    val counter = system.actorOf(Props[Counter])

    counter ! Cmd(Zero(0))

    expectMsg(State(0))

    counter ! Cmd(Increment(3))

    expectMsg(State(3))
/*
    counter ! Cmd(Increment(5))

    counter ! Cmd(Decrement(3))

    counter ! "print"

    Thread.sleep(1000)
  */



  }

}
