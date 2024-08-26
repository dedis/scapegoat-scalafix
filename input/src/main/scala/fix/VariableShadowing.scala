/*
rule = VariableShadowing
 */
package fix

object VariableShadowing {

  def foo1 = {
    val a = 1
    def boo = {
      val a = 2 // assert: VariableShadowing
      println(a)
    }
  }

  class Test2 {
    val a = 1
    def foo = {
      val a = 2 // assert: VariableShadowing
      println(a)
    }
  }

  class Test3 {
    val a = 1
    def foo(b: Int) = b match {
      case a => println(a) // assert: VariableShadowing
    }
  }

  class Test4 {
    val a = 1
    def foo(b: Int) = b match {
      case f =>
        def boo() = {
          val a = 2 // assert: VariableShadowing
          println(a)
        }
    }
  }

  class Test5 {
    def foo(a: Int) = {
      val a = 1 // assert: VariableShadowing
      println(a)
    }
  }

  class Test6 {
    def foo(a: Int) = {
      println(a)
      def boo = {
        val a = 2 // assert: VariableShadowing
        println(a)
      }
    }
  }

  class Test7 {
    def foo = {
      val a = 1 // scalafix: ok;
      println(a)
    }
    def boo = {
      val a = 2 // scalafix: ok;
      println(a)
    }
  }

  class Test8 {
    val something = 4
    if (1 > 0) {
      val something = 4 // assert: VariableShadowing
      println(something + 1)
    } else {
      val something = 2 // assert: VariableShadowing
      println(something + 2)
    }
  }

  class Test9 {
    if (1 > 0) {
      val something = 4 // scalafix: ok;
      println(something + 1)
    } else {
      val something = 2 // scalafix: ok;
      println(something + 2)
    }
  }

  class Test11 {
    val possibility: Option[Int] = Some(3)
    possibility match {
      case Some(x) =>
        val y = x + 1 // scalafix: ok;
        println(y)
      case None =>
        val y = 0 // scalafix: ok;
        println(y)
    }
  }

  class Test12 {
    val possibility: Option[Int] = Some(3)
    possibility match {
      case Some(x) =>
        val y = x + 1 // scalafix: ok;
        println(y)
      case None => println("None")
    }
  }

  final case class A(value: String) // scalafix: ok;
  final case class B(value: String) // scalafix: ok;
  final case class C(value: Int) // scalafix: ok;

  for (i <- 1 to 10) println(i.toString) // scalafix: ok;
  for (i <- 1 to 10) println(i.toString) // scalafix: ok;

  for {
    c <- "Hello, world!" // scalafix: ok;
    if c != ','
  } println(c)

  object Test1 {

    def f(x: Int): String = x.toString
    def g(y: String): Int = y.toInt

    val a = Seq(1, 2, 3) // scalafix: ok;
    System.out.println(
      a // scalafix: ok;
        .map(s => f(s))
        .map(s => g(s))
    )
  }

  class TestVariableShadowing {
    private def testCallbackFunction1(shadowedArg: Long): Boolean = shadowedArg > 10
    private def testCallbackFunction2(arg: Long): Boolean = arg < 10

    private def testCaller(renamedArg: Long, func: Long => Boolean): Boolean = func(renamedArg)

    def test(shadowedArg: AnyVal): Boolean =
      shadowedArg match {
        case l1: Long   => testCaller(l1, testCallbackFunction1) // scalafix: ok;
        case l2: Double => testCaller(l2.toLong, testCallbackFunction2)
        case l3         => false
      }
  }

}
