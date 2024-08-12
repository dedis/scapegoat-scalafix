/*
rule = LooksLikeInterpolatedString
 */
package fix

import fix.Test.a

object LooksLikeInterpolatedString {
  def test(): Unit = {
    val str = "this is my $interpolated string lookalike" // assert: LooksLikeInterpolatedString

    val str1 = "this is my ${interpolated.string} lookalike" // assert: LooksLikeInterpolatedString

    val str3 = "this is my not interpolated string lookalike" // scalafix: ok;

    val a = "hello"
    val str4 = s"${a}" // scalafix: ok;

    val answer = 42
    val message = s"$$answer = $answer" // scalafix: ok;

    val str5 = q"${a}" // scalafix: ok;

  }

  implicit class QQuotes(val sc: StringContext) extends AnyVal {
    def q(args: Any*): String = "anything"
  }
}

object Test {
  val a = "hello"


}