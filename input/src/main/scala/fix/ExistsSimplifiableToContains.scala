/*
rule = ExistsSimplifiableToContains
 */
package fix

object ExistsSimplifiableToContains {
  val exists1 = List(1, 2, 3).exists(x => x == 2) // assert: ExistsSimplifiableToContains
  val exists12 = List(1, 2, 3).exists(x => 2 == x) // assert: ExistsSimplifiableToContains

  val list = List("sam", "spade")
  val exists2 = list.exists(_ == "spoof") // assert: ExistsSimplifiableToContains
  val exists22 = list.exists("spoof" == _) // assert: ExistsSimplifiableToContains

  val exists3 = (1 to 3).exists(_ == 2) // assert: ExistsSimplifiableToContains
  val exists32 = (1 to 3).exists(2 == _) // assert: ExistsSimplifiableToContains
  val exists33 = (1 until 3).exists(_ == 2) // assert: ExistsSimplifiableToContains
  val exists34 = (1 until 3).exists(2 == _) // assert: ExistsSimplifiableToContains

  def isItA(strings: String*): Boolean = {
    strings.exists { element => // assert: ExistsSimplifiableToContains
      element.toLowerCase == "a"
    }
  }

  val l: Iterable[String] = List[String]("a", "b", "c")
  print(l.exists(_ == "a")) // scalafix: ok;

  def atLeastOneIsAllLowercase(strings: String*): Boolean = {
    strings.exists { element => // scalafix: ok;
      element == element.toLowerCase
    }
  }

  def containsNoA(strings: String*): Boolean = {
    strings.exists { element => // scalafix: ok;
      element.replaceAll("a", "").size == element.size
    }
  }

  val map = Map("answer" -> "42")
  val isCorrect = map.exists(_._2 == "42") // scalafix: ok;

}
