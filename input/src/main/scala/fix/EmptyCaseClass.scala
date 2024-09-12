/*
rule = EmptyCaseClass
 */
package fix

object EmptyCaseClass {
  case class Empty() // assert: EmptyCaseClass
  case class Empty2() {} // assert: EmptyCaseClass

  case class Empty3() { // assert: EmptyCaseClass
    def foo = "boo"
  }

  case class NonEmpty() { // scalafix: ok;
    def foo = "boo"
    val myVal = 42
  }
  class NonEmpty2() // scalafix: ok;

  case class Empty4(name: String) // scalafix: ok;
  case object Empty5 // scalafix: ok;

  abstract class Attr(val name: String) {
    override def toString: String = name
    override def hashCode: Int = name.hashCode
    override def equals(obj: Any): Boolean = false
  }
  // We don't look at case classes extending something because we could have some fields in the parent class
  case class TestClass() extends Attr("test") // scalafix: ok;
  case class TestClass2(bool: Boolean) extends Attr("test") // scalafix: ok;
}
