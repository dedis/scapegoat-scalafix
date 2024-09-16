/*
rule = FindAndNotEqualsNoneReplaceWithExists
 */
package fix

object FindAndNotEqualsNoneReplaceWithExists {
  def test(): Unit = {
    List(1, 2, 3).find(_ > 0) != None // assert: FindAndNotEqualsNoneReplaceWithExists
    List(1, 2, 3).find(_ > 0) == None // assert: FindAndNotEqualsNoneReplaceWithExists
    List(1, 2, 3).find(_ > 0) // scalafix: ok;
  }

}
