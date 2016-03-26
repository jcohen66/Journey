package parentchild

import akka.actor.{ActorRef, ActorSystem, Props, Terminated}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class FaultHandlingDocSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("FaultHandlingDocSpec",
    ConfigFactory.parseString(
      """
      akka {
        loggers = ["akka.testkit.TestEventListener"]
        loglevel = "WARNING"
      }
      """)))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A supervisor" must "apply the chosen strategy for its child" in {

    val supervisor = system.actorOf(Props[Supervisor], "supervisor")

    supervisor ! Props[Child]
    val child = expectMsgType[ActorRef] // retrieve answer from TestKit’s testActor  }

    child ! 42 // set state to 42
    child ! "get"
    expectMsg(42)

    // The first test shall demonstrate the Resume directive, so we try it out by setting
    // some non-initial state in the actor and have it fail:
    child ! new ArithmeticException // crash it
    child ! "get"
    expectMsg(42)

    // As you can see the value 42 survives the fault handling directive. Now, if we change
    // the failure to a more serious NullPointerException, that will no longer be the case:
    child ! new NullPointerException // crash it harder
    child ! "get"
    expectMsg(0)

    //  in case of the fatal IllegalArgumentException the child will be terminated by the supervisor:
    watch(child) // have testActor watch “child”
    child ! new IllegalArgumentException // break it
    expectMsgPF() { case Terminated(`child`) => () }
  }

  "Supervisor" must "escalate the failure in the event of an exception" in {
    val supervisor = system.actorOf(Props[Supervisor], "supervisor-2")

    supervisor ! Props[Child] // create new child
    val child2 = expectMsgType[ActorRef]
    watch(child2)
    child2 ! "get" // verify it is alive
    expectMsg(0)

    child2 ! new Exception("CRASH") // escalate failure
    expectMsgPF() {
      case t @ Terminated(`child2`) if t.existenceConfirmed => ()
    }
  }

  "Child" must "survive the escalated restart" in {
    val supervisor2 = system.actorOf(Props[Supervisor2], "supervisor-3")

    supervisor2 ! Props[Child]
    val child3 = expectMsgType[ActorRef]

    child3 ! 23
    child3 ! "get"
    expectMsg(23)

    child3 ! new Exception("CRASH")
    child3 ! "get"
    expectMsg(0)
  }
}
