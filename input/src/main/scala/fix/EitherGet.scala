/*
rule = EitherGet
 */
package fix

object EitherGet {

  val l = Left("l")
  l.left.get // assert: EitherGet
  val r = Right("r")
  r.right.get // assert: EitherGet

  Left("l").left.get // assert: EitherGet
  Right("r").right.get // assert: EitherGet

}
