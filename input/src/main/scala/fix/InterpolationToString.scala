/*
rule = InterpolationToString
 */
package fix

object InterpolationToString {

  val a = 1
  println(s" ${a.toString} b c ") // assert: InterpolationToString
  println(s" ${2.toString} b ") // assert: InterpolationToString

  println(f"${2.toString} b") // assert: InterpolationToString
  println(f"${a.toString} b") // assert: InterpolationToString

  println(s" $a b c ") // scalafix: ok;
  println(f"$a b") // scalafix: ok;

  println(s"${a == 2} b") // scalafix: ok;
  println(s"${a == 2} b") // scalafix: ok;
}
