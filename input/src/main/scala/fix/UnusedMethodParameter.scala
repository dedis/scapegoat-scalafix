/*
rule = UnusedMethodParameter
 */
package fix

import scala.annotation.unused

object UnusedMethodParameter {
  def foo(a: String, b: Int, c: Int): Unit = { // assert: UnusedMethodParameter
    println(b)
    foo(a, b, b)
  }

  class Test1 {
    val initstuff = "sammy" // assert: UnusedMethodParameter
    val test = ??? // scalafix: ok;

    def foo(a: String, b: Int, c: Int): Unit = { // assert: UnusedMethodParameter
      println(b)
      foo(a, b, b)
    }
  }

  class Test2 {
    @SuppressWarnings(Array("all"))
    def foo(a: String, b: Int, c: Int): Unit = { // scalafix: ok;
      println(b)
      foo(a, b, b)
    }
  }

  class Test3 {
    def main(args: Array[String]): Unit = {} // scalafix: ok;
  }

  abstract class EventBusMessage(messageVersion: Int) // scalafix: ok;

  class Test4 {
    def bar(name: String): Nothing = throw new RuntimeException // scalafix: ok;
    def foo(name: String) = throw new RuntimeException // scalafix: ok;
  }

  // This test doesn't work on Scalameta / Scalafix since the type is inferred to be String, not nothing
  // Same goes for js.native test (not included here, see "for js.native defined method" test in UnusedMethodParameterTest in Scapegoat repo)
//  class Test5 {
//    def foo(name: String): String = throw new RuntimeException // scalafix: ok;
//  }

  trait Foo {
    def foo(name: String): String
  }
  object Fool extends Foo {
    override def foo(name: String): String = "sam" // scalafix: ok;
  }

  trait Foo2 {
    def foo(name: String): String // scalafix: ok;
  }
  object Fool2 extends Foo2 {
    def foo(name: String): String = "sam" // scalafix: ok;
  }

  trait Foo3 {
    def foo(name: String): String // scalafix: ok;
  }
  case class Fool3() extends Foo3 {
    def foo(name: String): String = "sam" // scalafix: ok;
  }

  case class Foo4(x: Int)

  case class Foo5(x: Int) // scalafix: ok;
  (y: Int) // assert: UnusedMethodParameter

  case class Foo6(x: Int)(y: Int) { // scalafix: ok;
    def example: String = {
      s"x = $x, y = $y"
    }
  }

  case class Foo7(x: Int)(y: Int) { // scalafix: ok;
    println(s"x = $x, y = $y")
    def example: String = "irrelevant"
  }

  class Foo8(x: Int) // assert: UnusedMethodParameter

  class Foo9(x: Int) // assert: UnusedMethodParameter
  (y: Boolean) // assert: UnusedMethodParameter

  class Foo10(x: Int) { // scalafix: ok;
    def example: String = {
      s"x = $x"
    }
  }

  class Foo11(x: Int) { // scalafix: ok;
    println(s"x = $x")

    def example: String = "irrelevant"
  }

  class Foo12(val x: Int) // scalafix: ok;

  class Foo13(var x: Int) // scalafix: ok;

  class Foo14(
      val x: Int, // scalafix: ok;
      y: Int // assert: UnusedMethodParameter
  )

  class Foo15(var x: Int)(
      val y: Int, // scalafix: ok;
      z: Int // assert: UnusedMethodParameter
  )

  class Foo16(@unused x: Int) // scalafix: ok;

  // The difference here is that they are in an object not a class
  def foo(x: Int) = 42 // assert: UnusedMethodParameter
  def fooUnused(@unused x: Int) = 42 // scalafix: ok;

  class Foo17 {
    def foo(x: Int) = 42 // assert: UnusedMethodParameter
    def fooUnused(@unused x: Int) = 42 // scalafix: ok;
  }
}
