/*
rule = ComparisonToEmptySet
 */
package fix

object ComparisonToEmptySet {
  val a = Set(1, 2, 3)

  val b = a == Set() // assert: ComparisonToEmptySet
  val c = Set() == a // assert: ComparisonToEmptySet

  val d = a == Set.empty // assert: ComparisonToEmptySet
  val e = Set.empty == a // assert: ComparisonToEmptySet

  val h = Set.apply() == a // assert: ComparisonToEmptySet
  val i = a == Set.apply() // assert: ComparisonToEmptySet

  val j = a == Set(3, 4) // scalafix: ok;
  val k = Set(3, 4) == a // scalafix: ok;
  val l = a != Set(3, 4) // scalafix: ok;
  val m = Set(3, 4) != a // scalafix: ok;
}
