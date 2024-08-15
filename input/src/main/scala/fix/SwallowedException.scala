/*
rule = SwallowedException
 */
package fix

object SwallowedException {

  def test(): Unit = {
    try {} catch {
      case e: Exception => // assert: SwallowedException
    }

    try {} catch {
      case e: Exception => // assert: SwallowedException
      case e: Throwable => // assert: SwallowedException
    }

    try {} catch {
      case e: Exception => println("a") // scalafix: ok;
      case e: Throwable => // assert: SwallowedException
    }

    try {
      println(Integer.valueOf("asdf"))
    } catch {
      case nfe: NumberFormatException => // assert: SwallowedException
        throw new IllegalArgumentException("not a number")
    }

    def error(e: Exception, str: String): Unit = println("Log ERROR " + str + " " + e)
    try {
      println(Integer.valueOf("asdf"))
    } catch {
      case nfe2: NumberFormatException => // assert: SwallowedException
        error(nfe2, "Invalid format")
        throw new IllegalStateException("it's not a number")
    }

    try {} catch {
      case e: RuntimeException => println("a") // scalafix: ok;
      case f: Exception        => println("b") // scalafix: ok;
      case x: Throwable        => println("c") // scalafix: ok;
    }

    try { println() }
    catch { case ignored: Exception => } // scalafix: ok;

    try { println() }
    catch { case ignore: Exception => } // scalafix: ok;

    try {
      println(Integer.valueOf("asdf"))
    } catch {
      case nfe: NumberFormatException =>
        throw new IllegalArgumentException("not a number", nfe) // scalafix: ok;
    }
  }

}
