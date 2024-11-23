/*
rule = FindDotIsDefined
 */
package fix

object FindDotIsDefined {
  def test(): Unit = {
    val a = List(1, 2, 3).find(_ > 4).isDefined // assert: FindDotIsDefined
    val b = List(1, 2, 3).find(_ > 4) // scalafix: ok;
  }

}
