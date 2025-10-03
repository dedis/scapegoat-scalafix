/*
rule = ModOne
 */
package fix

object ModOne {
  def test(): Unit = {
    val a = 5 % 1 // assert: ModOne
    val b = 1 % 5 // scalafix: ok;

    val c = a % 1 // assert: ModOne
    val d = 1 % a // scalafix: ok;
  }

}
