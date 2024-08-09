/*
rule = EmptyIfBlock
 */
package fix
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object EmptyIfBlock {
  def test(): Unit = {
    if (true) { // assert: EmptyIfBlock
    }

    if (true) { // assert: EmptyIfBlock
      ()
    }

    if (1 > 2) {
      println("sammy") // scalafix: ok;
    }

    Future("test").map(value => {
      if (value.contains("foo")) { // scalafix: ok;
      } else {
        throw new IllegalStateException("Bar!")
      }
    })

    Future("test").map(value => {
      if (value.contains("foo")) { // scalafix: ok;
        ()
      } else {
        throw new IllegalStateException("Bar!")
      }
    })
  }
}
