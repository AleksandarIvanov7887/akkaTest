sealed trait Elem[+T] {
  def data: T
  def next: Elem[T]
}
case class DataElem[+T](data: T, next: Elem[T]) extends Elem[T]
case object NullElem extends Elem[Nothing] {
  override def data: Nothing = throw new NoSuchElementException("head of empty list")
  override def next: Elem[Nothing] = throw new UnsupportedOperationException("tail of empty list")
}

class CircularLinkedList[T](elems: List[T]) {
  private def create(elems: List[T]): Elem[T] = elems match {
    case Nil      => NullElem
    case x :: xs  => DataElem(x, create(elems.tail))
  }

  val head: Elem[T] = create(elems)
}

object CircularListMain extends App {
  val l = List(1, 2, 3)
  lazy val lazyList: LazyList[Int] = l.to(LazyList).lazyAppendedAll(lazyList)

  var lazyListVar: LazyList[Int] = lazyList

  println(lazyListVar.head)
  lazyListVar = lazyListVar.tail
  println(lazyListVar.head)
  lazyListVar = lazyListVar.tail
  println(lazyListVar.head)
  lazyListVar = lazyListVar.tail
  println(lazyListVar.head)
  lazyListVar = lazyListVar.tail
  println(lazyListVar.head)
  lazyListVar = lazyListVar.tail
  println(lazyListVar.head)
  lazyListVar = lazyListVar.tail
  println(lazyListVar.head)
  lazyListVar = lazyListVar.tail
}
