/*
rule = AvoidSizeEqualsZero
 */
package fix

object AvoidSizeEqualsZero {
  def test() = {
    val isEmpty1 = List(1).size == 0 // assert: AvoidSizeEqualsZero
    val isEmpty2 = List(1).length == 0 // assert: AvoidSizeEqualsZero
    val isEmpty3 = Set(1).size == 0 // assert: AvoidSizeEqualsZero
    val isEmpty5 = Seq(1).size == 0 // assert: AvoidSizeEqualsZero
    val isEmpty6 = Seq(1).length == 0 // assert: AvoidSizeEqualsZero

    case class Duration(start: Long, stop: Long) {
      def length: Long = stop - start
      def isEmpty: Boolean = length == 0 // scalafix: ok;
    }
  }
}
