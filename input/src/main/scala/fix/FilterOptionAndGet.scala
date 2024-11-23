/*
rule = FilterOptionAndGet
 */
package fix

object FilterOptionAndGet {
  def test(): Unit = {
    val a = List(None, Some("sam")).filter(_.isDefined).map(_.get) // assert: FilterOptionAndGet
    val b = List(None, Some("sam")).filter(_.isDefined).map(_.getOrElse("default")) // scalafix: ok;

  }

}
