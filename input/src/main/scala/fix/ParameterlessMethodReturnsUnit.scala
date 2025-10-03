/*
rule = ParameterlessMethodReturnsUnit
 */
package fix

object ParameterlessMethodReturnsUnit {
  def paramless: Unit = () // assert: ParameterlessMethodReturnsUnit
  def paramless2 = () // assert: ParameterlessMethodReturnsUnit

  def params(): Unit = () // scalafix: ok; since it has parentheses
  def paramsInt: Int = 3 // scalafix: ok; since it doesn't return Unit
  def paramsIntWithoutType = 3 // scalafix: ok; since it doesn't return Unit
}
