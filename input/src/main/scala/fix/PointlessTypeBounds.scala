/*
rule = PointlessTypeBounds
 */
package fix

object PointlessTypeBounds {
  class Test1[A >: Nothing] // assert: PointlessTypeBounds
  class Test2[A <: Any] // assert: PointlessTypeBounds

  object Test {
    def foo1[A <: Any] = {} // assert: PointlessTypeBounds
    def foo2[A >: Nothing] = {} // assert: PointlessTypeBounds
  }

  class Test3 {
    implicit class Monster1[T >: Nothing](t: T) { // assert: PointlessTypeBounds
      def bite: T = t
    }

    implicit class Monster2[T <: Any](t: T) { // assert: PointlessTypeBounds
      def bite: T = t
    }
  }

  class Test4[A <: Exception] // scalafix: ok; Exception is not Any
  class Test5 // scalafix: ok; no bounds

  class Test6[A, B] // scalafix: ok; multiple type params

  trait Foo
  class Test7[A, B <: Foo] // scalafix: ok; multiple type params with single bound

  trait Boo
  class Test8[A >: Boo, B <: Foo] // scalafix: ok; multiple type params with multiple bounds

  object Test9 {
    def foo1[A >: String] = {} // scalafix: ok; lower bound is not Nothing
    def foo2[B <: Thread] = {} // scalafix: ok; upper bound is not Any
    def foo3 = {} // scalafix: ok; no bounds
  }

  implicit class Monster[T](t: T) { // scalafix: ok; no bounds
    def bite: T = t
  }

  type AnyHow
  class Test10[T <: AnyRef, S] // scalafix: ok; upper bound is AnyRef not Any
  class Test11[T <: AnyRef, S] // scalafix: ok; upper bound is AnyRef not Any
  class Test12[T <: AnyRef, S] // scalafix: ok; upper bound is AnyRef not Any
  class Test13[T <: AnyRef, S] // scalafix: ok; upper bound is AnyRef not Any
  class Test14[S, T <: AnyRef] // scalafix: ok; upper bound is AnyRef not Any
  class Test15[S, T <: AnyHow] // scalafix: ok; upper bound is AnyHow not Any
}
