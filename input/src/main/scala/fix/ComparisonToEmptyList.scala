/*
rule = ComparisonToEmptyList
 */
package fix

object ComparisonToEmptyList {
  val a = List(1, 2, 3)

  val b = a == List() // assert: ComparisonToEmptyList
  val c = List() == a // assert: ComparisonToEmptyList

  val d = a == List.empty // assert: ComparisonToEmptyList
  val e = List.empty == a // assert: ComparisonToEmptyList

  val f = a == Nil // assert: ComparisonToEmptyList
  val g = Nil == a // assert: ComparisonToEmptyList

  val h = List.apply() == a // assert: ComparisonToEmptyList
  val i = a == List.apply() // assert: ComparisonToEmptyList

  val j = a == List(3, 4) // scalafix: ok;
  val k = List(3, 4) == a // scalafix: ok;
  val l = a != List(3, 4) // scalafix: ok;
  val m = List(3, 4) != a // scalafix: ok;

}
