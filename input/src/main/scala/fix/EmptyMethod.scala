/*
rule = EmptyMethod
 */
package fix

object EmptyMethod {
  private def foo = {} // assert: EmptyMethod
  private def foo2 = true // scalafix: ok;
  private def foo3 = { // assert: EmptyMethod
    ()
  }
  private def foo4 = { // scalafix: ok;
    println("sammy")
    ()
  }

  private def foo5 = () // assert: EmptyMethod

  trait A { def foo = () } // scalafix: ok;

  class Animal {
    def makeSound(): Unit = {} // scalafix: ok;
  }

  class Dog extends Animal {
    override def makeSound(): Unit = { // scalafix: ok;
      println("Bark")
    }
  }

}
