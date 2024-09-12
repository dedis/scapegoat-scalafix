/*
rule = DuplicateSetValue
 */
package fix

object DuplicateSetValue {
  Set("sam", "aylesbury", "sam") // assert: DuplicateSetValue
  Set("name", "location", "aylesbury", "bob")

  // Works for different types of literals
  Set(1, 2, 1) // assert: DuplicateSetValue
  Set(2.4, 3.5, 2.4) // assert: DuplicateSetValue
  Set(2.4f, 3.5f, 2.4f) // assert: DuplicateSetValue
  Set(true, false, true) // assert: DuplicateSetValue
  Set('a', 'b', 'a') // assert: DuplicateSetValue
  Set("olivia", 2, "olivia") // assert: DuplicateSetValue
  Set(2L, 3, 2L) // assert: DuplicateSetValue

  def name = "could be random" // We could have def name calling random.nextInt, hence we exclude variables and only check literals
  Set(name, "middle", name) // scalafix: ok;
}
