/*
rule = RepeatedIfElseBody
 */
package fix

object RepeatedIfElseBody {

  def test(): Unit = {
    val a = "input"
    println(if (a.length > 3) { // assert: RepeatedIfElseBody
      "long string"
    } else {
      "long string"
    })

    if (1 > 0) { // assert: RepeatedIfElseBody
      val len = a.length
      println(s"Length is $len")
    } else {
      val len = a.length
      println("I won't say")
    }

    println(if (a.length > 3) { // scalafix: ok;
      "long string"
    } else {
      "short string"
    })

    if (a.length > 3) println(42) // assert: RepeatedIfElseBody
    else println(42)

    if (a.length > 3) println("olivia") // scalafix: ok;
    else println(42)
  }

}
