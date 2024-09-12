/*
rule = AsInstanceOf
 */
package fix

object AsInstanceOf {
  // In the original Scapegoat test code, there are tests with SuppressWarning annotations.
  // We chose to not include these as the same could be done for every rule. If a user wants to suppress a rule, they can simply remove it from the configuration.
  def test(): Unit = {
    val s: Any = "sammy"
    println(s.asInstanceOf[String]) // assert: AsInstanceOf

    case class MappingCharFilter(name: String, mappings: (String, String)*) // scalafix: ok;

    val pf: PartialFunction[Any, Unit] = {
      case s: String        => println(s) // scalafix: ok;
      case i: Int if i == 4 => println(i) // scalafix: ok;
      case _                => println("no match :(") // scalafix: ok;
    }

    sealed trait MyGADT[T]
    final case class VariantInt(value: Int) extends MyGADT[Int]
    final case class VariantString(value: String) extends MyGADT[String]

    def doStuff[T](gadt: MyGADT[T]): T = { // scalafix: ok;
      gadt match {
        case VariantInt(value)    => value
        case VariantString(value) => value
      }
    }
  }

}
