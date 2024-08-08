/*
rule = AvoidSizeNotEqualsZero
 */
package fix

object AvoidSizeNotEqualsZero {
  def test() = {
    val isEmpty1 = List(1).size != 0 // assert: AvoidSizeNotEqualsZero
    val isEmpty2 = List(1).length != 0 // assert: AvoidSizeNotEqualsZero
    val isEmpty3 = Set(1).size != 0 // assert: AvoidSizeNotEqualsZero
    val isEmpty4 = Seq(1).size != 0 // assert: AvoidSizeNotEqualsZero
    val isEmpty5 = Seq(1).length != 0 // assert: AvoidSizeNotEqualsZero
    val isGreater1 = List(1).size > 0 // assert: AvoidSizeNotEqualsZero
    val isGreater2 = List(1).length > 0 // assert: AvoidSizeNotEqualsZero
    val isGreater3 = Set(1).size > 0 // assert: AvoidSizeNotEqualsZero
    val isGreater4 = Seq(1).size > 0 // assert: AvoidSizeNotEqualsZero
    val isGreater5 = Seq(1).length > 0 // assert: AvoidSizeNotEqualsZero

    case class Duration(start: Long, stop: Long) {
      def length: Long = stop - start
      def nonEmpty: Boolean = length > 0 // scalafix: ok;
    }
  }
}
