package caching

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.testkit._
import org.scalatest._

import scala.concurrent.duration._



import org.scalamock.scalatest._

class UserActorTest extends TestKit(ActorSystem("test-system"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with MockFactory
  with BeforeAndAfterAll {

  import UserActor._
  import DataActor.{ UserData, UserCredits }

  private val userId = 1

  "The UserActor" when {
    "getting GetUserData message" should {
      "cache the result when cache is enabled" in withActor() { (actor, dataActorProbe) ⇒
        val userData = UserData("user name")

        actor ! GetUserData
        dataActorProbe.expectMsg(DataActor.GetUserData(userId))
        dataActorProbe.reply(userData)
        expectMsg(userData)

        actor ! GetUserData
        dataActorProbe.expectNoMsg()
        expectMsg(userData)
      }

      "NOT cache the result when cache is not enabled" in withActor(cache = false) { (actor, dataActorProbe) ⇒
        val userData = UserData("user name")

        actor ! GetUserData
        dataActorProbe.expectMsg(DataActor.GetUserData(userId))
        dataActorProbe.reply(userData)
        expectMsg(userData)

        actor ! GetUserData
        dataActorProbe.expectMsg(DataActor.GetUserData(userId))
        dataActorProbe.reply(userData)
        expectMsg(userData)
      }
    }

    "getting GetUserCredits message" should {
      "NOT cache the result even if cache is enabled" in withActor() { (actor, dataActorProbe) ⇒
        val userCredits = UserCredits(10)

        actor ! GetUserCredits
        dataActorProbe.expectMsg(DataActor.GetUserCredits(userId))
        dataActorProbe.reply(userCredits)
        expectMsg(userCredits)

        actor ! GetUserCredits
        dataActorProbe.expectMsg(DataActor.GetUserCredits(userId))
        dataActorProbe.reply(userCredits)
        expectMsg(userCredits)
      }
    }
  }

  private def withActor(cache: Boolean = true)(testCode: (ActorRef, TestProbe) ⇒ Any): Unit = {
    val dataActorProbe = TestProbe()
    class TestUserActor(userId: Int, dataActorRef: ActorRef) extends UserActor(userId, dataActorRef) {
      override val cacheDuration: FiniteDuration = if (cache) FiniteDuration(1, MINUTES) else Duration.Zero
    }

    val actor = system.actorOf(Props(new TestUserActor(userId, dataActorProbe.ref)))

    try {
      testCode(actor, dataActorProbe)
    } finally {
      system.stop(actor)
    }
  }

  override protected def afterAll(): Unit = system.shutdown()
}
