/*
rule = FinalizerWithoutSuper
 */
package fix

import scala.annotation.nowarn

@nowarn
object FinalizerWithoutSuper { // noinspection ScalaDeprecation

  // DISABLED FOR NOW
  // Finalize is deprecated and causes compilation errors, despite @nowarn annotation
//  class Test1 {
//    override def finalize = {} // assert: FinalizerWithoutSuper
//  }
//
//  class Test2 {
//    override def finalize = { // assert: FinalizerWithoutSuper
//      println("sam")
//    }
//  }
//
//  class Test3 {
//    override def finalize = { // scalafix: ok;
//      super.finalize()
//      println("sam")
//    }
//  }
//
//  class Test4 {
//    override def finalize = { // scalafix: ok;
//      println("sam")
//      super.finalize()
//      println("sam")
//    }
//  }
//
//  class Test5 {
//    override def finalize = { // scalafix: ok;
//      println("sam")
//      super.finalize()
//    }
//  }
}
