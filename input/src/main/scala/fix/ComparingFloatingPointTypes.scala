/*
rule = ComparingFloatingPointTypes
 */
package fix

object ComparingFloatingPointTypes {
  def test(): Unit = {
    val f1 = 1.46456f
    val f2 = 1.46456f
    if (f1 == f2) { // assert: ComparingFloatingPointTypes
      print("Equal!")
    } else {
      print("Not equal")
    }
    val d1 = 1.546456
    val d2 = 1.546456
    if (d1 == d2) { // assert: ComparingFloatingPointTypes
      print("Equal!")
    } else {
      print("Not equal!")
    }
    f1 == d1 // assert: ComparingFloatingPointTypes
    f1.equals(f2) // assert: ComparingFloatingPointTypes
    d1.equals(d2) // assert: ComparingFloatingPointTypes
  }

}
