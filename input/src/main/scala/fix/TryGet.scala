/*
rule = TryGet
 */
package fix

import scala.util.Try

object TryGet {
  def test(): Unit = {
    val o = Try { println("mojave") }
    o.get // assert: TryGet

    Try { println("ghost") }.get // assert: TryGet
  }
}
