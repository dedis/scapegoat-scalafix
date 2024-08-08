/*
rule = ComparingUnrelatedTypes
 */
package fix

object ComparingUnrelatedTypes {
  def test(): Boolean = {
      val stringOption: Option[String] = Some("1")
      val intOption: Option[Int] = Some(1)
      stringOption == intOption // assert: ComparingUnrelatedTypes

      val o1 = Some("1")
      val o2 = Some("2")
      o1 == o2 // scalafix: ok;

      Enum1.Value1 == Enum2.Value1 // assert: ComparingUnrelatedTypes
      Enum1.Value1 != Enum2.Value1 // assert: ComparingUnrelatedTypes

      Enum1.Value1 == Enum1.Value1 // scalafix: ok
      Enum1.Value1 != Enum1.Value2 // scalafix: ok
      val a : A = new A()
      val b: B = new B()

    a == b // scalafix: ok


//      Some(2) == Some("2")
  }

  object Enum1 extends Enumeration {
    val Value1, Value2, Value3 = Value
  }

  object Enum2 extends Enumeration {
    val Value1, Value2, Value3 = Value
  }

  class A {
    val l = 100l
    val b = 0 == l
  }

  class B extends A {
    val c = 'a'
  }

}
