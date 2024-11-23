/*
rule = FinalModifierOnCaseClass
 */
package fix

object FinalModifierOnCaseClass {
  case class Person(name: String) // assert: FinalModifierOnCaseClass
  abstract case class Person2(name: String) // scalafix: ok;
  final case class Person3(name: String) // scalafix: ok;

  // Comes from an issue on Scapegoat GitHub
  sealed abstract case class Nat(toInt: Int) // scalafix: ok;
  object Nat {
    def fromInt(n: Int): Option[Nat] =
      if (n >= 0) Some(new Nat(n) {}) else None
  }
}
