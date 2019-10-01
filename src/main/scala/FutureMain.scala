import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FutureMain extends App {

  val a = Some(Future {
    Thread.sleep(1000)
  })

  println(a.getClass)

  Thread.sleep(3000)

  println(a.get.isCompleted)

}
