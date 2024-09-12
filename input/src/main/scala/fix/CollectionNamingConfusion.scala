/*
rule = CollectionNamingConfusion
 */
package fix

object CollectionNamingConfusion {
  def test(): Unit = {

    // Incorrect names
    val list = Set(1, 2, 3) // assert: CollectionNamingConfusion
    val set = List(1, 2, 3) // assert: CollectionNamingConfusion
    val vector = Seq(1, 2, 3) // assert: CollectionNamingConfusion
    val seq = Vector(1, 2, 3) // assert: CollectionNamingConfusion
    val array = Map(1 -> "one", 2 -> "two") // assert: CollectionNamingConfusion
    val map = Array(1, 2, 3) // assert: CollectionNamingConfusion

    // Longer incorrect names
    val listSet = Set(1, 2, 3) // assert: CollectionNamingConfusion
    val listVector = Vector(1, 2, 3) // assert: CollectionNamingConfusion
    val listSeq = Seq(1, 2, 3) // assert: CollectionNamingConfusion
    val listArray = Array(1, 2, 3) // assert: CollectionNamingConfusion
    val listMap = Map(1 -> "one", 2 -> "two") // assert: CollectionNamingConfusion
    var listSetVar = Set(1, 2, 3) // assert: CollectionNamingConfusion

    val setList = List(1, 2, 3) // assert: CollectionNamingConfusion
    val setVector = Vector(1, 2, 3) // assert: CollectionNamingConfusion
    val setSeq = Seq(1, 2, 3) // assert: CollectionNamingConfusion
    val setArray = Array(1, 2, 3) // assert: CollectionNamingConfusion
    val setMap = Map(1 -> "one", 2 -> "two") // assert: CollectionNamingConfusion
    var setVectorVar = Vector(1, 2, 3) // assert: CollectionNamingConfusion

    val vectorList = List(1, 2, 3) // assert: CollectionNamingConfusion
    val vectorSet = Set(1, 2, 3) // assert: CollectionNamingConfusion
    val vectorSeq = Seq(1, 2, 3) // assert: CollectionNamingConfusion
    val vectorArray = Array(1, 2, 3) // assert: CollectionNamingConfusion
    val vectorMap = Map(1 -> "one", 2 -> "two") // assert: CollectionNamingConfusion
    var vectorSeqVar = Seq(1, 2, 3) // assert: CollectionNamingConfusion

    val seqList = List(1, 2, 3) // assert: CollectionNamingConfusion
    val seqSet = Set(1, 2, 3) // assert: CollectionNamingConfusion
    val seqVector = Vector(1, 2, 3) // assert: CollectionNamingConfusion
    val seqArray = Array(1, 2, 3) // assert: CollectionNamingConfusion
    val seqMap = Map(1 -> "one", 2 -> "two") // assert: CollectionNamingConfusion
    var seqArrayVar = Array(1, 2, 3) // assert: CollectionNamingConfusion

    val arrayList = List(1, 2, 3) // assert: CollectionNamingConfusion
    val arraySet = Set(1, 2, 3) // assert: CollectionNamingConfusion
    val arrayVector = Vector(1, 2, 3) // assert: CollectionNamingConfusion
    val arraySeq = Seq(1, 2, 3) // assert: CollectionNamingConfusion
    val arrayMap = Map(1 -> "one", 2 -> "two") // assert: CollectionNamingConfusion
    var arrayMapVar = Map(1 -> "one", 2 -> "two") // assert: CollectionNamingConfusion

    val mapList = List(1, 2, 3) // assert: CollectionNamingConfusion
    val mapSet = Set(1, 2, 3) // assert: CollectionNamingConfusion
    val mapVector = Vector(1, 2, 3) // assert: CollectionNamingConfusion
    val mapSeq = Seq(1, 2, 3) // assert: CollectionNamingConfusion
    val mapArray = Array(1, 2, 3) // assert: CollectionNamingConfusion
    var mapListVar = List(1, 2, 3) // assert: CollectionNamingConfusion

    // Correct names
    val myList = List(1, 2, 3) // scalafix: ok;
    val mySet = Set(1, 2, 3) // scalafix: ok;
    val myVector = Vector(1, 2, 3) // scalafix: ok;
    val mySeq = Seq(1, 2, 3) // scalafix: ok;
    val myArray = Array(1, 2, 3) // scalafix: ok;
    val myMap = Map(1 -> "one", 2 -> "two") // scalafix: ok;
    var myListVar = List(1, 2, 3) // scalafix: ok;

    // Normal names
    val currentLBListenerSettings = List(1) // scalafix: ok;
    val exclusionsForAdvancedSetup = List(2, 3) // scalafix: ok;
    val preSetupSteps = List(4) // scalafix: ok;
  }

}
