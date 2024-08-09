/*
rule = DuplicateImport
 */
package fix

object DuplicateImport {
  def test(): Unit = {
    import scala.concurrent.duration.TimeUnit
    import scala.concurrent.duration.TimeUnit // assert: DuplicateImport

    import scala.collection.immutable.Set
    import scala.collection.immutable.{Set, Seq} // assert: DuplicateImport
    object Test {}

    object A {
      import scala.collection.immutable.Set
    }
    class B {
      import scala.collection.immutable.Set // scalafix: ok;
    }
  }
}
