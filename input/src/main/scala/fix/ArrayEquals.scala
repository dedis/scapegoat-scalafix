/*
rule = ArrayEquals
 */
package fix

object ArrayEquals {
  def test(): Unit = {
    val a = Array(1, 2, 3)
    val b = Array(1, 2, 3)
    println(a == b) // assert: ArrayEquals
    println(a != b) // assert: ArrayEquals

    println(Array(1, 2, 3) == Array(1, 2, 3)) // assert: ArrayEquals
    println(Array(1, 2, 3) != Array(1, 2, 3)) // assert: ArrayEquals

    // Null comparison shouldn't lead to warnings
    println(a != null) // scalafix: ok;
    println(a == null) // scalafix: ok;
    println(Array(1, 2, 3) != null) // scalafix: ok;
    println(Array(1, 2, 3) == null) // scalafix: ok;

    println(a sameElements b) // scalafix: ok;
    println(Array(1, 2, 3) sameElements Array(1, 2, 3)) // scalafix: ok;

    case class StemmerOverrideTokenFilter(name: String, rules: Array[String]) // scalafix: ok;
  }

}
