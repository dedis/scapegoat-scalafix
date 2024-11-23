/*
rule = FilterDotHead
 */
package fix

object FilterDotHead {
  def test(): Unit = {
    List(1, 2, 3).filter(_ < 0).head // assert: FilterDotHead
    List(1, 2, 3).map(e => e).head // scalafix: ok;
  }

}
