/*
rule = MethodReturningAny
 */
package fix

object MethodReturningAny {

  def inferredAny(value: Boolean) = { // assert: MethodReturningAny
    if (value) "This is a string" // returns a String
    else 42 // returns an Int
  }

  // This method returns Any in Scala 2 and should be flagged. It returns Int | String in Scala 3 and will also be flagged.
  // See rule for more precisions.

  def test: Any = 1 // assert: MethodReturningAny

  def foo: Int = 4 // scalafix: ok;
  def boo: String = "sam" // scalafix: ok;

  trait A {
    def foo: AnyRef = "foo" // assert: MethodReturningAny
  }
  class B extends A {
    override def foo: AnyRef = "overridden foo" // scalafix: ok;
  }

}
