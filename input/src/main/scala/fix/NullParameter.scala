/*
rule = NullParameter
 */
package fix

object NullParameter {

  println(null) // assert: NullParameter

  trait A

  def a(b: A) = {}
  a(null) // assert: NullParameter
  def c(d: String)(f: A) = {}
  c("a")(null) // assert: NullParameter

  def normal(a: String) = {}
  normal("test") // scalafix: ok;


  abstract class Super(val name: String)
  case class Boo(override val name: String) extends Super(name) // scalafix: ok;
  class Birds(names:String*) // scalafix: ok;

}
