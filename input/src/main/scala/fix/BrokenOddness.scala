/*
rule = BrokenOddness
 */
package fix

object BrokenOddness {
  def test(): Unit = {
    val i = 15
    def odd(a: Int) = a % 2 == 1 // assert: BrokenOddness
    val odd2 = i % 2 == 1 // assert: BrokenOddness
    println(-3 % 2 == 1) // assert: BrokenOddness

    println(i % 2 != 0) // scalafix: ok;
  }
}
