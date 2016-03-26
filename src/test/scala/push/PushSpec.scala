package push

import akka.actor.{Actor, ActorSystem}
import akka.testkit.{TestActorRef, TestProbe, TestKit, ImplicitSender}
import org.scalatest.{FlatSpecLike, BeforeAndAfterAll, MustMatchers}
import push.SimpleActor.GetData

/**
  * Created by jcohe_000 on 3/24/2016.
  */
class PushSpec extends TestKit(ActorSystem("test-system"))
  with ImplicitSender
  with FlatSpecLike
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val probe1 = TestProbe()
  implicit val other = Some(probe1.r ef)

  "SimpleActor" should "Send a message to the probe" in {

    val myActor = TestActorRef(new SimpleActor(), "simple")

    myActor ! GetData

    probe1.expectMsg(GetData)
  }
}


class AnotherActor extends Actor {
  def receive = {
    case _ =>
  }
}
