/*
rule = WhileTrue
 */
package fix

object WhileTrue {

  while (true) { // assert: WhileTrue
    println("sam")
  }

  while (System.currentTimeMillis > 0) { // scalafix: ok;
    println("sam")
  }

}
