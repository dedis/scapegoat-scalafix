/*
rule = RepeatedCaseBody
 */
package fix

object RepeatedCaseBody {

  def test(): Unit = {
    val s: Any = null
    s match {
      // Minimum to trigger rule is a body of size >= 4 lines
      case "sam" => println("foo"); println("foo"); println("foo"); println("foo") // scalafix: ok;
      case "bam" => println("foo"); println("foo"); println("foo"); println("foo") // assert: RepeatedCaseBody
      case _     =>
    }

    s match {
      case str: String if str.length == 3 => println("foo"); println("foo"); println("foo"); println("foo") // scalafix: ok;
      case str: String                    => println("foo"); println("foo"); println("foo"); println("foo") // assert: RepeatedCaseBody
      case i: Int                         => println("foo"); println("foo"); println("foo"); println("foo") // assert: RepeatedCaseBody
      case _                              =>
    }

    s match {
      case "sam" => println("foo"); println("foo"); println("foo"); println("foo") // scalafix: ok;
      case "bam" => println("foo"); println("other"); println("foo"); println("foo") // scalafix: ok;
      case _     =>
    }

    s match {
      case s: String => println("foo"); // scalafix: ok;
      case _         => println("foo"); // scalafix: ok;
    }
  }

}
