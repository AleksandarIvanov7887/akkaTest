import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

class TestTheKitSpec extends TestKit(ActorSystem("MySpec"))
  with ImplicitSender {

}
