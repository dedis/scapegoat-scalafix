/*
rule = ClassNames
 */
package fix

object ClassNames {
  class aClass // assert: ClassNames
  case class bClass() // assert: ClassNames

  class My_class // assert: ClassNames
  case class Your_class() // assert: ClassNames

  class A { // scalafix: ok;
    def update(): Unit = {}
  }
  // Anonymus class
  println(new A { // scalafix: ok;
    override def update(): Unit = super.update()
  })
}
