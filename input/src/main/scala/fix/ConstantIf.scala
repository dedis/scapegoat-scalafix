/*
rule = ConstantIf
 */
package fix

object ConstantIf {
  def test() = {
    if (1 < 2) { // assert: ConstantIf
      println("sammy")
    }
    if (2 < 1) { // assert: ConstantIf
      println("sammy")
    }
    if ("sam" == "sam".substring(0)) println("sammy") // scalafix: ok; while it is easy to see this is a constant,
    // it's not trivial to determine that
    if (true) println("sammy") // assert: ConstantIf
    if (false) println("sammy") // assert: ConstantIf
    if (1 < System.currentTimeMillis()) println("sammy") // scalafix: ok; same here, chances are this is a constant

    val a = 3
    val b = 5
    if (a < b) println("scooby") // scalafix: ok;

    while ( true ) { println("sam") } // scalafix: ok;
  }
}