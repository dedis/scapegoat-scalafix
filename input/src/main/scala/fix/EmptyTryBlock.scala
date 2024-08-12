/*
rule = EmptyTryBlock
 */
package fix

object EmptyTryBlock {
  try { // assert: EmptyTryBlock
  } catch {
    case r: RuntimeException => throw r
    case e: Exception        =>
    case t: Throwable        =>
  }

  try () // assert: EmptyTryBlock
  catch {
    case r: RuntimeException => throw r
  }

  try { // assert: EmptyTryBlock
    ()
  } catch {
    case e: NotImplementedError =>
    case t: Throwable           =>
  }

  try { // scalafix: ok;
    getClass
  } catch {
    case r: RuntimeException => throw r
    case e: Exception        =>
    case t: Throwable        =>
  }

}
