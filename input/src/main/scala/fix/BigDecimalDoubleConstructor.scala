/*
rule = BigDecimalDoubleConstructor
 */
package fix

object BigDecimalDoubleConstructor {
  def test(): Unit = {
    BigDecimal(0.1d) // assert: BigDecimalDoubleConstructor
    BigDecimal(0.1f) // assert: BigDecimalDoubleConstructor

    val d = 0.1d
    BigDecimal(d) // assert: BigDecimalDoubleConstructor

    val f = 0.1f
    BigDecimal(f) // assert: BigDecimalDoubleConstructor

    BigDecimal(100) // scalafix: ok;

    new java.math.BigDecimal(0.5d) // assert: BigDecimalDoubleConstructor
    new java.math.BigDecimal(0.5f) // assert: BigDecimalDoubleConstructor
    new java.math.BigDecimal(d) // assert: BigDecimalDoubleConstructor
    new java.math.BigDecimal(f) // assert: BigDecimalDoubleConstructor

    new java.math.BigDecimal(100) // scalafix: ok;
  }
}
