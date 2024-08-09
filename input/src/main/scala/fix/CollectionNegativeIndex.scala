/*
rule = CollectionNegativeIndex
 */
package fix

object CollectionNegativeIndex {
  def test() = {
    List(1, 2, 3)(-1) // assert: CollectionNegativeIndex
    Seq(1, 2, 3)(-2) // assert: CollectionNegativeIndex
    val s: Seq[Int] = Array(1, 2, 3)
    s(-1) // assert: CollectionNegativeIndex

    List(1, 2, 3)(1) // scalafix: ok;
    Seq(1, 2, 3)(2) // scalafix: ok;

    IndexedSeq(1, 2, 3)(-1) // assert: CollectionNegativeIndex
    Array(1, 2, 3)(-1) // assert: CollectionNegativeIndex
    Vector(1, 2, 3)(-4) // assert: CollectionNegativeIndex

  }

}
