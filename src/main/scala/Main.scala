import scala.collection.mutable

object Main extends App {
//  val config = ConfigFactory.load()
//  implicit val system: ActorSystem = ActorSystem("ActorSystem1", config.getConfig("actorSystem1"))
//  implicit val execContext: ExecutionContextExecutor = system.getDispatcher
//  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val mapz: mutable.Map[String, (String, Int, Long)] = mutable.Map.empty

  mapz += ("a" -> ("a", 0, 0L), "b" -> ("b", 2, 4L), "c" -> ("c", 4, 8L))

  val li = List((3, "a"), (4, "b"), (5, "b"), (5, "b"))

  println(mapz)

  val r1 = li.groupBy[String](_._2).map[String, (String, Int, Long)] { case (key, value) => (key, (mapz(key)._1, value.size, mapz(key)._3))}

  println(r1)

  mapz ++= r1
  println(mapz)

  def extractGuid(tsGuid: String): String = {
    tsGuid.split("_").tail.headOption match {
      case Some(guid) => guid
      case None => throw new IllegalStateException(s"Cannot extract guid from tsGuid: $tsGuid")
    }
  }

  println(extractGuid("1232534_guidddddd"))
//  println(extractGuid("1232534guidddddd"))
//  println(extractGuid(""))


  var a = ("asd", 1)

  val result: Int = a match {
    case ("afd", n) => n

    case ("asd", n) => n match {
      case 1 => 1
      case _ => 2
    }

  }

  println(result)

  val set: mutable.Set[Int] = mutable.Set(1, 2, 3)

  println(set.remove(4))
}
