/*
rule = FilterDotSize
 */
package fix

object FilterDotSize {
  def test(): Unit = {
    List(1, 2, 3).filter(_ < 0).size // assert: FilterDotSize
    List(1, 2, 3).map(e => e).size // scalafix: ok;
  }

}
