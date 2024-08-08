/*
rule = UnsafeContains
 */
package fix

object UnsafeContains {
  def test() = {

//    List(1,2,3).contains("olivia") // assert: UnsafeContains SCALA 3 only


    val l = List(1, 2, 3)

    l.contains(2) // scalafix: ok;

    l contains "olivia" // assert: UnsafeContains

    l.contains(4) // scalafix: ok;

    val l2 = List("olivia")

    l2.contains(2) // assert: UnsafeContains

    l2.contains("olivia") // scalafix: ok;



    l.contains(2.1) // assert: UnsafeContains

    val s1 = Seq(2,3,4)
    s1.contains(3) // scalafix: ok;
    s1.contains("olivia") // assert: UnsafeContains

    val o = Option(2)
    o.contains(2) // scalafix: ok;
    o.contains("olivia") // assert: UnsafeContains

    val s2 = Some(2)
    s2.contains(2) // scalafix: ok;
    s2.contains("olivia") // assert: UnsafeContains

    val words = Seq("Hello", "world")
    val moreWords = Seq("Goodbye", "cruel", "world")
    val common = moreWords.filter(words.contains) // scalafix: ok;

    val common2 = moreWords.filter(v => words.contains(v)) // scalafix: ok;

    def distinctIndices(a: Seq[Int], b: Seq[Int]): Boolean = !a.exists(b.contains) // scalafix: ok;

    def distinctIndices2(a: Seq[Int], b: Seq[Int]): Boolean = !a.exists(v => b.contains(v)) // scalafix: ok;
  }

  def test(xs: List[Int], y1: Int) = xs.contains(y1) // scalafix: ok;
  def test2(xs: List[String], y: Int) = xs.contains(y) // assert: UnsafeContains

  def f1[A](xs: Seq[A], y: A)                                        = xs.contains(y)  // scalafix: ok;
  def f2[A <: AnyRef](xs: Seq[A], y: Int)                            = xs.contains(y)  // assert: UnsafeContains
  def f3[A <: AnyRef](xs: Vector[A], y: Int)                         = xs.contains(y) // assert: UnsafeContains
  def f4[CC[X] <: Seq[X], A](xs: CC[A], y: A)                        = xs contains y  // scalafix: ok;
  def f5[CC[X] <: Seq[X], A <: AnyRef, B <: AnyVal](xs: CC[A], y: B) = xs.contains(y)  // assert: UnsafeContains

  class C {
    def f[A](xs: Seq[A], y: A) = xs contains y // scalafix: ok;
  }
}

