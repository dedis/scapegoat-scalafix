/*
rule = ComparisonWithSelf
 */
package fix

object ComparisonWithSelf {

  def test(): Unit = {
    val c = false
    val b = true
    if (b == b) { // assert: ComparisonWithSelf
    } else if (b != b) { // assert: ComparisonWithSelf
    } else if (c == b) {} // scalafix: ok;
    else {}
  }

}
