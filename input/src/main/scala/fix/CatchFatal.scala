/*
rule = CatchFatal
 */
package fix

object CatchFatal {
  def test(): Unit = {
    try {} catch {
      case e: Exception           => // scalafix: ok;
      case _: VirtualMachineError => // assert: CatchFatal
    }

    try {} catch {
      case e: RuntimeException => // scalafix: ok;
      case x: ThreadDeath      => // assert: CatchFatal
      case f: Exception        => // scalafix: ok;
    }

    try {} catch {
      case e: RuntimeException => // scalafix: ok;
      case f: Exception        => // scalafix: ok;
    }
  }
}
