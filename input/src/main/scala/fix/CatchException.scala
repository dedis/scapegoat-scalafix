/*
rule = CatchException
 */
package fix

import scala.annotation.nowarn

object CatchException {
  def test(): Unit = {
    try {} catch {
      case _: Exception => // assert: CatchException
      case t: Throwable => // scalafix: ok;
    }

    try {} catch {
      case e: RuntimeException => // scalafix: ok;
      case x: Exception        => // assert: CatchException
      case f: Throwable        => // scalafix: ok;
    }

    try {} catch {
      case e: RuntimeException => // scalafix: ok;
      case f: Throwable        => // scalafix: ok;
    }
  }
}
