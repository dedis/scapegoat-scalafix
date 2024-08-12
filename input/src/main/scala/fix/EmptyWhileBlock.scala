/*
rule = EmptyWhileBlock
 */
package fix

object EmptyWhileBlock {
  while (true) () // assert: EmptyWhileBlock
  while (true) {} // assert: EmptyWhileBlock

  while (true) { () } // assert: EmptyWhileBlock
  while (true) { println("sam") } // scalafix: ok;
}
