/*
rule = SuspiciousMatchOnClassObject
 */
package fix

object SuspiciousMatchOnClassObject {
  def test(): Unit = {
    abstract case class Test2(name: String)
    trait Machine
    case class Terminator(name: String) extends Machine

    class Test {
      def b: Any = 4

      b match {
        case Terminator => // assert: SuspiciousMatchOnClassObject
        case _ =>
      }
    }
  }

  def test2(): Unit = {
    class Test {
      def b: Any = 4

      b match {
        case Terminator => // assert: SuspiciousMatchOnClassObject
        case _ =>
      }
    }

    trait Machine // Declared after usage
    case class Terminator(name: String) extends Machine
  }

  def test3() = {
    trait Machine
    case class Terminator() extends Machine

    class Test {
      def b : Any = 4
      b match {
        case Terminator => // assert: SuspiciousMatchOnClassObject
        case _ =>
      }
    }
  }

  def test4() = {
    trait Machine
    case class Terminator(name: String) extends Machine
    case class Android(name: String) extends Machine
    case class Man(name: String, gender: String) extends Machine

    object Test {
      val b: Any = Terminator("arnie")
      b match {
        case _: Man => // scalafix: ok;
        case _ => // scalafix: ok;
      }
    }
  }

  def test5() = {
    trait Machine
    case class Terminator(name:String) extends Machine
    case class Android(name:String) extends Machine

    object Test {
      val b : Any = Terminator("arnie")
      b match {
        case Android("data") => println("yay data") // scalafix: ok;
        case _ =>
      }
    }
  }

  def test6() = {
    trait Machine
    case class Terminator(name:String) extends Machine

    object Test {
      val b : Any = Terminator("arnie")
      b match {
        case Terminator => // assert: SuspiciousMatchOnClassObject
        case _ =>
      }
    }
  }

  def test7() = {
    trait Android
    case object Lal extends Android
    case object Data extends Android
    case class Robot(name:String) extends Android

    object Test {
      val b : Any = Data
      b match {
        case Data => println("Yes captain") // scalafix: ok;
        case Lal => println("Yes dad") // scalafix: ok;
        case Robot(name) => println(name) // scalafix: ok;
        case _ =>
      }
    }
  }

  def test8() = {
    trait TestTrait
    object TestObject extends TestTrait

    object Go {
      def test(t: TestTrait): Unit = t match {
        case TestObject ⇒ println("ok") // scalafix: ok;
      }
    }
  }

  def test9() = {
    object Go {
      trait TestTrait
      object TestObject extends TestTrait

      def test(t: TestTrait): Unit = t match {
        case TestObject ⇒ println("ok") // scalafix: ok;
      }
    }
  }

  def test10() = {
    abstract class Kill
    case object Kill extends Kill
    object A {
      val a: AnyRef = "sam"
      a match {
        case Kill => // scalafix: ok;
        case _ =>
      }
    }
  }

  def test11() = {
    case class Robot(name:String)
    object A {
      val Robbie = Robot("Robbie")
      val a: AnyRef = Robot("t800")
      a match {
        case Robbie =>
        case Robot(name) =>
        case _ =>
      }
    }
  }

  def test12() = {
    class MyClass
    object MyClass

    object Test {
      val a: AnyRef = new MyClass
      a match {
        case MyClass => // assert: SuspiciousMatchOnClassObject
        case _ =>
      }
    }
  }

  def test13() = {
    abstract class MyClass2
    object MyClass2

    object Test {
      val a: AnyRef = new MyClass2 {}
      a match {
        case MyClass2 => // scalafix: ok;
        case _ =>
      }
    }

  }




}
