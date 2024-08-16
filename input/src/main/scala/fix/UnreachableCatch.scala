/*
rule = UnreachableCatch
 */
package fix

import scala.annotation.nowarn

object UnreachableCatch {

  @nowarn // Some unreachable warning may appear thanks to Scala built-in warnings.
  // Since we are testing the rule, we can ignore them.
  def test(): Unit = {
    try {
    } catch {
      case _ : Throwable => // scalafix: ok;
      case e : Exception => // assert: UnreachableCatch
    }

    // Test case 2 with unreachability due to guard
    try {
    } catch {
      case e: RuntimeException => // scalafix: ok;
      case e: RuntimeException if e.getMessage.contains("foo") => // assert: UnreachableCatch
    }

    try {
    } catch {
      case e : RuntimeException => // scalafix: ok;
      case f : Exception => // scalafix: ok;
      case x : Throwable => // scalafix: ok;
    }

    try {
    } catch {
      case e: RuntimeException if e.getMessage.contains("foo") => // scalafix: ok;
      case e: RuntimeException => // scalafix: ok;
    }

    try {
    } catch {
      case e: RuntimeException if e.getMessage.contains("foo") => // scalafix: ok;
      case e: RuntimeException if e.getMessage.contains("bar") => // scalafix: ok;
    }
  }

}
