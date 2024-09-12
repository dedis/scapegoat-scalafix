/*
rule = DoubleNegation
 */
package fix

object DoubleNegation {
  val b = true
  val c = !(!b) // assert: DoubleNegation
  val d = !(!(!b)) // assert: DoubleNegation
  val f = !b // scalafix: ok;

}
