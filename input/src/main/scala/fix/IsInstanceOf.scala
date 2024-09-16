/*
rule = IsInstanceOf
 */
package fix

object IsInstanceOf {
  def test(): Unit = {
    val s: Any = "sammy"
    println(s.isInstanceOf[String]) // assert: IsInstanceOf

    case class MappingCharFilter(name: String, mappings: (String, String)*) // scalafix: ok;

    val pf: PartialFunction[Any, Unit] = {
      case s: String => println(s) // scalafix: ok;
      case i: Int if i == 4 => println(i) // scalafix: ok;
      case _ => println("no match :(") // scalafix: ok;
    }

    @SuppressWarnings(Array("all"))
    def hello: Unit = {
      val s: Any = "sammy"
      println(s.isInstanceOf[String]) // scalafix: ok;
    }

    @SuppressWarnings(Array("IsInstanceOf"))
    def hello2: Unit = {
      val s: Any = "sammy"
      println(s.isInstanceOf[String]) // scalafix: ok;
    }


    sealed trait MyGADT[T]
    final case class VariantInt(value: Int) extends MyGADT[Int]
    final case class VariantString(value: String) extends MyGADT[String]

    def doStuff[T](gadt: MyGADT[T]): T = { // scalafix: ok;
      gadt match {
        case VariantInt(value) => value
        case VariantString(value) => value
      }
    }
  }

}
