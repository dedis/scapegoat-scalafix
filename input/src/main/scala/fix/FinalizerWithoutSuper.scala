/*
rule = FinalizerWithoutSuper
 */
package fix

import scala.annotation.nowarn

@nowarn
object FinalizerWithoutSuper { //noinspection ScalaDeprecation

  class Test1 {
    override def finalize = {} // assert: FinalizerWithoutSuper
  }

  class Test2 {
    override def finalize = { // assert: FinalizerWithoutSuper
      println("sam")
    }
  }

  class Test3 {
    override def finalize = { // scalafix: ok;
      super.finalize()
      println("sam")
    }
  }

  class Test4 {
    override def finalize = { // scalafix: ok;
      println("sam")
      super.finalize()
      println("sam")
    }
  }

  class Test5 {
    override def finalize = { // scalafix: ok;
      println("sam")
      super.finalize()
    }
  }
}
