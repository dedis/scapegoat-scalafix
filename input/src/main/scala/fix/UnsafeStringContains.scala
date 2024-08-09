/*
rule = UnsafeStringContains
 */
package fix

object UnsafeStringContains {
  def test(): Unit = {
    "abcdefgh".contains(2) // assert: UnsafeStringContains
    "abcdefgh".contains(2: Char) // scalafix: ok;
    "abcdefgh".contains(2.asInstanceOf[Char]) // scalafix: ok;
    "abcdefgh".contains(new Object) // assert: UnsafeStringContains
    val str: String = ""
    str.contains(2L) // assert: UnsafeStringContains
    "abcd".contains("abc") // scalafix: ok;
    "abcd".contains('b') // scalafix: ok;
    str.contains("abc") // scalafix: ok;
    str.contains('b') // scalafix: ok;
    Seq("one", "two", "three").forall("abcdefgh".contains) // scalafix: ok;
  }
}
