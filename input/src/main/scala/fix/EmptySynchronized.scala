/*
rule = EmptySynchronized
 */
package fix

object EmptySynchronized {
  synchronized { // scalafix: ok;
    println("sammy")
  }
  val a = { // scalafix: ok;
    println("sammy")
  }
  synchronized { // assert: EmptySynchronized
  }
  val b = { // scalafix: ok;
  }

  synchronized(()) // assert: EmptySynchronized

  synchronized { () } // assert: EmptySynchronized

}
