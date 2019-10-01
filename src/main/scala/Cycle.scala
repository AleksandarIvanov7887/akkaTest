object Cycle extends App {

  val l = List("a", "b")
  val l2 = List("c", "d")

  val result = for {
    number <- 0 to 1
    letter <- l(number)
    letter2 <- l2(number)
  } yield {
    (letter, letter2)
  }

  println(result)


  var a = 7
  a match {
    case 1 => println(1)
    case 7 | _ => println(7)
    case 4 => println(8)
  }
}
