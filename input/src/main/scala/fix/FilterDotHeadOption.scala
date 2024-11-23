/*
rule = FilterDotHeadOption
 */
package fix

object FilterDotHeadOption {
  def test(): Unit = {
    List(1, 2, 3).filter(_ < 0).headOption // assert: FilterDotHeadOption
    List(1, 2, 3).map(e => e).headOption // scalafix: ok;
  }

}
