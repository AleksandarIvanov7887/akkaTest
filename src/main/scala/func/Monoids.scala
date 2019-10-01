package func

import cats.Monoid
import cats.instances.string._
import cats.instances.option._
import cats.instances.int._
import cats.instances.double._
import cats.syntax.semigroup._
import cats.Semigroup

trait MySemigroup[A] {
  def combine(a: A, b: A): A
}

trait MyMonoid[A] extends MySemigroup[A] {
  def empty: A
}

object MyMonoid {
  def apply[A](implicit monoid: MyMonoid[A]) =
    monoid
}

object MonoidInstances {
  implicit val oneBoolMonoid: MyMonoid[Boolean] = new MyMonoid[Boolean] {
    override def empty: Boolean = false

    override def combine(a: Boolean, b: Boolean): Boolean = a || b
  }

  implicit def setMonoid[A]: MyMonoid[Set[A]] = new MyMonoid[Set[A]] {
    def empty: Set[A] = Set()
    def combine(a: Set[A], b: Set[A]): Set[A] = a ++ b
  }

  implicit val orderMonoid: Monoid[Order] = new Monoid[Order] {
    override def empty: Order = Order(0, 0)

    override def combine(x: Order, y: Order): Order =
      Order(x.totalCost + y.totalCost, x.quantity + y.quantity)
  }
}

case class Order(totalCost: Double, quantity: Double)

object SuperAdder {
  def add[A](items: List[A])(implicit m: Monoid[A]): A =
    items.foldLeft(m.empty)(m.combine)
}

object Monoids extends App {
  import SuperAdder._
  import MonoidInstances._

  Monoid[String].combine("Hi, ", "there")

  "Hi, " |+| "Sashe"

  println(SuperAdder.add(List(Option(1), Option(2))))
  println(SuperAdder.add(List(Order(1, 3), Order(2, 1))))

}
