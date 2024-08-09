/*
rule = OptionGet
 */
package fix

object OptionGet {
  def test(): Unit = {
    val o = Option("olivia")
    o.get // assert: OptionGet

    Option("layla").get // assert: OptionGet
  }
}
