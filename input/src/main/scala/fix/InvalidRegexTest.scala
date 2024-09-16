/*
rule = InvalidRegexTest
 */
package fix

object InvalidRegexTest {
  val regex = "?".r // assert: InvalidRegexTest
  val regex2 = "^[A-Z][A-Za-z0-9]*$".r // scalafix: ok;
}
