/*
rule = CatchThrowable
 */
package fix

object CatchThrowable {
  def test(): Unit = {
    try {} catch {
      case e: Exception => // scalafix: ok;
      case _: Throwable => // assert: CatchThrowable
    }

    try {} catch {
      case e: RuntimeException => // scalafix: ok;
      case f: Exception        => // scalafix: ok;
      case x: Throwable        => // assert: CatchThrowable
    }

    try {} catch {
      case e: RuntimeException => // scalafix: ok;
      case f: Exception        => // scalafix: ok;
    }
  }
}
