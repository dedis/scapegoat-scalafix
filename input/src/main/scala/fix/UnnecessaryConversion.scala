/*
rule = UnnecessaryConversion
 */
package fix

object UnnecessaryConversion {

  def test(): Unit = {
    "test".toString // assert: UnnecessaryConversion
    val s = "test"
    s.toString // assert: UnnecessaryConversion

    println(42.toInt) // assert: UnnecessaryConversion
    val i = 42
    i.toInt // assert: UnnecessaryConversion

    println(42L.toLong) // assert: UnnecessaryConversion
    val l = 42L
    l.toLong // assert: UnnecessaryConversion

    println('c'.toChar) // assert: UnnecessaryConversion
    val c = 'c'
    c.toChar // assert: UnnecessaryConversion

    // No literal byte value in Scala
    val b = Byte.MaxValue
    b.toByte // assert: UnnecessaryConversion

    // No literal short value in Scala
    val sh: Short = 42
    sh.toShort // assert: UnnecessaryConversion

    println(42f.toFloat) // assert: UnnecessaryConversion
    val f = 42f
    f.toFloat // assert: UnnecessaryConversion

    println(42.0.toDouble) // assert: UnnecessaryConversion
    val d = 42.0
    d.toDouble // assert: UnnecessaryConversion

    Set(1, 2, 3).toSet // assert: UnnecessaryConversion
    collection.mutable.Set(1, 2, 3).toSet // assert: UnnecessaryConversion
    collection.immutable.Set(1, 2, 3).toSet // assert: UnnecessaryConversion
    val set = Set(1, 2, 3)
    set.toSet // assert: UnnecessaryConversion

    Map(1 -> 2).toMap // assert: UnnecessaryConversion
    collection.mutable.Map(1 -> 2).toMap // assert: UnnecessaryConversion
    collection.immutable.Map(1 -> 2).toMap // assert: UnnecessaryConversion
    val map = Map(1 -> 2)
    map.toMap // assert: UnnecessaryConversion

    List(1, 2, 3).toList // assert: UnnecessaryConversion
    val list = List(1, 2, 3)
    list.toList // assert: UnnecessaryConversion

    Seq(1, 2, 3).toSeq // assert: UnnecessaryConversion
    collection.mutable.Seq(1, 2, 3).toSeq // assert: UnnecessaryConversion
    collection.immutable.Seq(1, 2, 3).toSeq // assert: UnnecessaryConversion
    val seq = Seq(1, 2, 3)
    seq.toSeq // assert: UnnecessaryConversion

    Vector(1, 2, 3).toVector // assert: UnnecessaryConversion
    val vector = Vector(1, 2, 3)
    vector.toVector // assert: UnnecessaryConversion

    Array(1, 2, 3).toArray // assert: UnnecessaryConversion
    Array.empty[Int].toArray // assert: UnnecessaryConversion
    val array = Array(1, 2, 3)
    array.toArray // assert: UnnecessaryConversion
  }

}
