/*
rule = FilterDotIsEmpty
 */
package fix

object FilterDotIsEmpty {
  def test(): Unit = {
    List(1, 2, 3).filter(_ < 0).isEmpty // assert: FilterDotIsEmpty
    List(1, 2, 3).map(e => e).isEmpty // scalafix: ok;
  }

}
