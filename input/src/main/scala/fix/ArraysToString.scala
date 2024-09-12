/*
rule = ArraysToString
 */
package fix

object ArraysToString {
  def test(): Unit = {
    Array(5).toString // assert: ArraysToString
    Array(5).toString() // assert: ArraysToString

    val array = Array(1, 2, 3, 4)
    array.toString // assert: ArraysToString
    array.toString() // assert: ArraysToString

    List(5).toString // scalafix: ok;
    List(5).toString() // scalafix: ok;

    val l = List(1, 2, 3, 4)
    l.toString // scalafix: ok;
    l.toString() // scalafix: ok;
  }

}
