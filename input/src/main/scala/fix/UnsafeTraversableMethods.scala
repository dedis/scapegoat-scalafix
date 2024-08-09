/*
rule = UnsafeTraversableMethods
 */
package fix

object UnsafeTraversableMethods {
  def test(): Unit = {
    val l = List(1, 2, 3)
    l.head // assert: UnsafeTraversableMethods
    l.tail // assert: UnsafeTraversableMethods
    l.init // assert: UnsafeTraversableMethods
    l.last // assert: UnsafeTraversableMethods
    l.reduce(_ + _) // assert: UnsafeTraversableMethods
    l.reduceLeft(_ + _) // assert: UnsafeTraversableMethods
    l.reduceRight(_ + _) // assert: UnsafeTraversableMethods
    l.max // assert: UnsafeTraversableMethods
    l.maxBy(x => x) // assert: UnsafeTraversableMethods
    l.min // assert: UnsafeTraversableMethods
    l.minBy(x => x) // assert: UnsafeTraversableMethods

    val s = Seq(1, 2, 3)
    s.head // assert: UnsafeTraversableMethods
    s.tail // assert: UnsafeTraversableMethods
    s.init // assert: UnsafeTraversableMethods
    s.last // assert: UnsafeTraversableMethods
    s.reduce(_ + _) // assert: UnsafeTraversableMethods
    s.reduceLeft(_ + _) // assert: UnsafeTraversableMethods
    s.reduceRight(_ + _) // assert: UnsafeTraversableMethods
    s.max // assert: UnsafeTraversableMethods
    s.maxBy(x => x) // assert: UnsafeTraversableMethods
    s.min // assert: UnsafeTraversableMethods
    s.minBy(x => x) // assert: UnsafeTraversableMethods

    val v = Vector(1, 2, 3)
    v.head // assert: UnsafeTraversableMethods
    v.tail // assert: UnsafeTraversableMethods
    v.init // assert: UnsafeTraversableMethods
    v.last // assert: UnsafeTraversableMethods
    v.reduce(_ + _) // assert: UnsafeTraversableMethods
    v.reduceLeft(_ + _) // assert: UnsafeTraversableMethods
    v.max // assert: UnsafeTraversableMethods
    v.maxBy(x => x) // assert: UnsafeTraversableMethods
    v.min // assert: UnsafeTraversableMethods
    v.minBy(x => x) // assert: UnsafeTraversableMethods

    val i = Iterable(1, 2, 3)
    i.head // assert: UnsafeTraversableMethods
    i.tail // assert: UnsafeTraversableMethods
    i.init // assert: UnsafeTraversableMethods
    i.last // assert: UnsafeTraversableMethods
    i.reduce(_ + _) // assert: UnsafeTraversableMethods
    i.reduceLeft(_ + _) // assert: UnsafeTraversableMethods
    i.max // assert: UnsafeTraversableMethods
    i.maxBy(x => x) // assert: UnsafeTraversableMethods
    i.min // assert: UnsafeTraversableMethods
    i.minBy(x => x) // assert: UnsafeTraversableMethods

    // Final tests to test that it also works on expressions, not just variables
    List(1, 2, 3).head // assert: UnsafeTraversableMethods
    Seq(1, 2, 3).head // assert: UnsafeTraversableMethods
    Vector(1, 2, 3).head // assert: UnsafeTraversableMethods
    Iterable(1, 2, 3).head // assert: UnsafeTraversableMethods

    List((1, 2)).head._1 // assert: UnsafeTraversableMethods

    class F(args: String*) // scalafix: ok;
  }

}
