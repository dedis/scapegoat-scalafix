/*
rule = EmptySynchronizedBlock
 */
package fix

object EmptySynchronizedBlock {
  synchronized { // scalafix: ok;
    println("sammy")
  }
  val a = { // scalafix: ok;
    println("sammy")
  }
  synchronized { // assert: EmptySynchronizedBlock
  }
  val b = { // scalafix: ok;
  }

  synchronized(()) // assert: EmptySynchronizedBlock

  synchronized { () } // assert: EmptySynchronizedBlock

}
