/*
rule = DivideByOne
 */
package fix

object DivideByOne {
  var a = 14
  val b = a / 1 // assert: DivideByOne
  a /= 1 // assert: DivideByOne
  a = a / 2 // scalafix: ok;
  a /= 2 // scalafix: ok;

  var c = 10.0
  val d = c / 1 // assert: DivideByOne
  c /= 1 // assert: DivideByOne
  c = c / 2 // scalafix: ok;
  c /= 2 // scalafix: ok;

  var e = 100L
  val f = e / 1 // assert: DivideByOne
  e /= 1 // assert: DivideByOne
  e = e / 2 // scalafix: ok;
  e /= 2 // scalafix: ok;

  var g = 5.0d
  val h = g / 1 // assert: DivideByOne
  g /= 1 // assert: DivideByOne
  g = g / 2 // scalafix: ok;
  g /= 2 // scalafix: ok;

}
