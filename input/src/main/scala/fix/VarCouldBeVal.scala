/*
rule = VarCouldBeVal
 */
package fix

import scala.util.Try

object VarCouldBeVal {
  def foo = {
    var count = 1 // assert: VarCouldBeVal
    println(count)
    for (k <- 1 until 10) {
      println(k + count)
    }
  }

  def foo2 = {
    var count = 1 // assert: VarCouldBeVal
    println(count)
    def boo(n: Int): Unit = {
      println(n + count)
    }
    for (k <- 1 until 10) {
      println(k + count)
    }
  }

  def foo3 = {
    var bar = 1 // assert: VarCouldBeVal
    val myValue = 2 // scalafix: ok;
    var baz = 3 // assert: VarCouldBeVal
  }

  def foo4 = {
    var count = 1 // scalafix: ok;
    if (true) count = 2
  }

  def foo5 = {
    var count = 1 // scalafix: ok;
    println(count)
    for (k <- 1 until 10) {
      count = count + 1
      println(k + count)
    }
  }

  def foo6 = {
    var count = 1 // scalafix: ok;
    println(count)
    def boo(n: Int): Unit = {
      count = n
      println(count)
    }
    for (k <- 1 until 10) {
      println(k + count)
    }
  }

  def foo7 = {
    var count = 1 // scalafix: ok;
    try {
      println("sam")
    } finally {
      count = 2
    }
  }

  def foo8 = {
    var count = 1 // scalafix: ok;
    if (count == 10) {
      println("sam")
    } else {
      count = count + 1
    }
  }

  def foo9 = {
    var count = 1 // scalafix: ok;
    count match {
      case 10 => println("sam")
      case _  => count = count + 1
    }
  }

  def foo10(b: Boolean): Int = {
    var count = 0 // scalafix: ok;
    if (b) count += 1
    count
  }

  def something(): List[String] = List("a")
  def foo11(): Unit = {
    var items = List.empty[String] // scalafix: ok;
    while ({
      items = something()
      items.size
    } < 10) {
      println(items)
    }
  }

  trait Iterator {
    def next: Int
  }
  object foo12 {
    val iterator = new Iterator {
      var last = -1 // scalafix: ok;

      def next: Int = {
        last = last + 1
        last
      }
    }
  }

  def foo13 = {
    val l = List(1, 2, 3)
    var modif = 0 // scalafix: ok;
    l.foreach {
      case i if i % 2 == 0 => modif = i
      case _               => ()
    }
  }

  def foo14 = {
    val l = List(1, 2, 3)
    var modif = 0 // scalafix: ok;
    l.foreach(e => modif = e)
  }

  def foo15 = {
    var a = 0 // assert: VarCouldBeVal
    if (a != 0) println("test")
    if (a >= 0) println("test")
    if (a <= 0) println("test")
  }

  def foo16 = {
    var a = 0 // scalafix: ok;
    if (a != 0) println("test")
    else {
      a *= 2
    }
  }

}
