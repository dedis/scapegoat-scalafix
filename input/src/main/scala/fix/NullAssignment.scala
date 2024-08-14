/*
rule = NullAssignment
 */
package fix

object NullAssignment {

  def test(): Unit = {
    var a = "sam"
    a = null // assert: NullAssignment

    val b = null // assert: NullAssignment

    var c = null // assert: NullAssignment
  }

  object Test {
    var a = "sam"
    a = null // assert: NullAssignment

    val b = null // assert: NullAssignment

    var c = null // assert: NullAssignment
  }

  def foo = null // assert: NullAssignment

}
