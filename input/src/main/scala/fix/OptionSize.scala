/*
rule = OptionSize
 */
package fix

object OptionSize {
  def test(): Unit = {
    val o = Option("olivia")
    o.size // assert: OptionSize

    Option("layla").size // assert: OptionSize
  }
}
