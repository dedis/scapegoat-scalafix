/*
rule = EmptyFor
 */
package fix

object EmptyFor {
  def test(): Unit = {
    for (k <- 1 to 100) { // assert: EmptyFor
    }

    for (k <- 1 to 100) { // assert: EmptyFor
      ()
    }

    for (k <- 1 to 100) { // scalafix: ok;
      println("sam")
    }
  }
}
