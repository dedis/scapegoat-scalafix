/*
rule = DuplicateMapKey
 */
package fix

object DuplicateMapKey {
  Map("name" -> "sam", "location" -> "aylesbury", "name" -> "bob") // assert: DuplicateMapKey
  Map("name" -> "sam", "location" -> "aylesbury", "name2" -> "bob") // scalafix: ok;

  val tuples = List((1, 2), (3, 4))
  Map(tuples: _*) // scalafix: ok;
}
