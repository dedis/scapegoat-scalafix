/*
rule = BooleanParameter
 */
package fix

object BooleanParameter {
  def foo(bool: Boolean) = 4 // assert: BooleanParameter

  def foo2(value: Int)(bool: Boolean) = 4 // assert: BooleanParameter

  def foo3(value: Int) = 4 // scalafix: ok;
  def foo3(value: Int)(dob: Double) = 4 // scalafix: ok;

  final case class Test(bool: Boolean) // scalafix: ok;
}
