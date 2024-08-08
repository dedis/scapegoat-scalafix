/*
rule = CatchExceptionImmediatelyRethrown
 */
package fix

object CatchExceptionImmediatelyRethrown {
  def test(): Unit = {
    try {
      val x = 1
    } catch {
      case foo: IllegalStateException => throw foo // assert: CatchExceptionImmediatelyRethrown
    }

    try {
      val x = 1
    } catch {
      case foo: Throwable => throw foo // assert: CatchExceptionImmediatelyRethrown
    }

    try {
      val x = 1
    } catch {
      case bar: IllegalCallerException => throw bar // assert: CatchExceptionImmediatelyRethrown
      case foo: Throwable              => throw foo // assert: CatchExceptionImmediatelyRethrown
    }

    try {
      val x = 1
    } catch {
      case bar: ArrayStoreException => throw new IllegalArgumentException("Not rethrowing") // scalafix: ok;
      case foo: Throwable           => println("Not rethrowing") // scalafix: ok;
    }
  }
}
