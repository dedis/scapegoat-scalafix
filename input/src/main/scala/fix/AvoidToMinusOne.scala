/*
rule = AvoidToMinusOne
 */
package fix

object AvoidToMinusOne {
  def test(): Unit = {
    val k = 10
    for (n <- 1 to k - 1) { // assert: AvoidToMinusOne
      println("sam")
    }

    for (n <- 1 to k - 2) { // scalafix: ok;
      println("sam")
    }

    for (n <- 1 until k - 1) { // scalafix: ok;
      println("sam")
    }
  }
}
