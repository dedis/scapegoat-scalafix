/*
rule = CollectionIndexOnNonIndexedSeq
 */
package fix

object CollectionIndexOnNonIndexedSeq {
  def test() = {
    val idx = 2
    List(1, 2, 3)(idx) // assert: CollectionIndexOnNonIndexedSeq
    Seq(1, 2, 3)(idx) // assert: CollectionIndexOnNonIndexedSeq
    val s: Seq[Int] = Array(1, 2, 3)
    s(idx) // assert: CollectionIndexOnNonIndexedSeq

    List(1, 2, 3)(1) // scalafix: ok;
    Seq(1, 2, 3)(2) // scalafix: ok;

    IndexedSeq(1, 2, 3)(idx) // scalafix: ok;
    Array(1, 2, 3)(idx) // scalafix: ok;
    Vector(1, 2, 3)(idx) // scalafix: ok;

  }

}
