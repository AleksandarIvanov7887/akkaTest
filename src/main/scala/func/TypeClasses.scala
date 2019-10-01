package func

import cats.Show
import cats.instances.all._
import cats.syntax.show._
import cats.syntax.eq._
import cats.syntax.option._
import java.util.Date
import cats.Eq

trait Printable[A] {
  def format(value: A): String
}

final case class Dog(name: String, age: Int, color: String)

object PrintableInstances {
  implicit val stringPrintable: Printable[String] = (value: String) => value

  implicit val intPrintable: Printable[Int] = (value: Int) => value.toString

  implicit val dogPrintable: Printable[Dog] =
    (dog: Dog) => {
      val name  = Printable.format(dog.name)
      val age   = Printable.format(dog.age)
      val color = Printable.format(dog.color)
      s"$name is a $age year-old $color dog"
    }
}

object PrintableSyntax {
  implicit class PrintableOps[A](value: A) {
    def format(implicit p: Printable[A]): String = p.format(value)

    def print(implicit p: Printable[A]): Unit = println(format(p))
  }
}

object Printable {
  def format[A](value: A)(implicit printable: Printable[A]): String = printable.format(value)

  def print[A](value: A)(implicit printable: Printable[A]): Unit = println(format(value))
}

object Implicitsss extends App {
  import PrintableInstances._
  import PrintableSyntax._

  val showInt: Show[Int] = Show.apply[Int]
  val showString: Show[String] = Show.apply[String]

  val intAsString: String = showInt.show(123)
  val stringAsString: String = showString.show("abv")

  implicit val dateShow: Show[Date] = new Show[Date] {
    override def show(t: Date): String = s"${t.getTime}ms since the epoch"
  }

  implicit val dateShow2: Show[Date] = Show.show(date => s"${date.getTime}ms since the epoch")

  implicit val dogShow: Show[Dog] = Show.show(dog => {
    val name  = dog.name.show
    val age   = dog.age.show
    val color = dog.color.show
    s"$name is a $age year-old $color dog"
  })

  val dog = Dog("Sharo", 10, "red")
  dog.print

  println(dog.show)


  val eqInt = Eq[Int]

  println(eqInt.eqv(123, 123))
  println(123 === 123)
  println(123 =!= 234)
//  123 =!= "234"
//  println(eqInt.eqv(123, "234"))

  (Some(1): Option[Int]) === (None: Option[Int])
  Option(1) === Option.empty[Int]
  1.some === none[Int]
  1.some =!= none[Int]

  implicit val dateEq: Eq[Date] = Eq.instance[Date] {
    (date1, date2) => date1.getTime === date2.getTime
  }

  val x = new Date()
  val y = new Date()

  x === x
  x === y

  implicit val dogEq: Eq[Dog] = Eq.instance[Dog] {
    (dog1, dog2) => (dog1.name === dog2.name ) && (dog1.age === dog2.age  ) && (dog1.color === dog2.color)
  }

  val dog1 = Dog("Garfield",   38, "orange and black")
  val dog2 = Dog("Heathcliff", 33, "orange and black")
  val optionDog1 = Option(dog1)
  val optionDog2 = Option.empty[Dog]
  dog1 === dog2
  optionDog1 === optionDog2
}
