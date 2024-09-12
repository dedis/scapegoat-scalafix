/*
rule = BoundedByFinalType
 */
package fix

object BoundedByFinalType {
  class Test[A <: String] // assert: BoundedByFinalType
  def foo[B <: Integer](): Unit = {} // assert: BoundedByFinalType

  val test: PartialFunction[Array[String], Int] = { // scalafix: ok;
    case _ => -1
  }
  val a = List[String]("sam") // scalafix: ok;

  class Test2[A <: Exception] // scalafix: ok;
  class Test3[A <: Any] // scalafix: ok;
  class Test4 // scalafix: ok;

  def foo2[B <: Exception](): Unit = {} // scalafix: ok;
  def foo3[B <: Any](): Unit = {} // scalafix: ok;
  def foo4(): Unit = {} // scalafix: ok;

  type Texty = String // scalafix: ok;
  type ListBuffer[A] = scala.collection.mutable.ListBuffer[A] // scalafix: ok;
}
