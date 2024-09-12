/*
rule = ArraysEquals
 */
package fix

object ArraysEquals {
  def test(): Unit = {
    val a = Array(1, 2, 3)
    val b = Array(1, 2, 3)
    println(a == b) // assert: ArraysEquals
    println(a != b) // assert: ArraysEquals
    // Null comparison shouldn't lead to warning
    println(a != null) // scalafix: ok;
    println(a == null) // scalafix: ok;

    println(a sameElements b) // scalafix: ok;

    case class StemmerOverrideTokenFilter(name: String, rules: Array[String]) // scalafix: ok;
  }

}
