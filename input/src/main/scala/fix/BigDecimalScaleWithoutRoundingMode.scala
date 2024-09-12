/*
rule = BigDecimalScaleWithoutRoundingMode
 */
package fix

import scala.math.BigDecimal.RoundingMode

object BigDecimalScaleWithoutRoundingMode {
  def test(): Unit = {
    val b = BigDecimal(10)
    b.setScale(2) // assert: BigDecimalScaleWithoutRoundingMode
    BigDecimal(10).setScale(2) // assert: BigDecimalScaleWithoutRoundingMode

    val b1 = new java.math.BigDecimal(2)
    b1.setScale(2) // assert: BigDecimalScaleWithoutRoundingMode
    new java.math.BigDecimal(2).setScale(2) // assert: BigDecimalScaleWithoutRoundingMode

    b.setScale(2, RoundingMode.UP) // scalafix: ok;
    BigDecimal(10).setScale(2, RoundingMode.DOWN) // scalafix: ok;
    new java.math.BigDecimal(2).setScale(2, RoundingMode.FLOOR) // scalafix: ok;

  }
}
