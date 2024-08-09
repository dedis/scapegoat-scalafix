/*
rule = CollectionPromotionToAny
 */
package fix

object CollectionPromotionToAny {
  def test() = {
    val a = List(1, 2, 3)
    val b = List(4, 5, 6)
    val c = a :+ b // assert: CollectionPromotionToAny

    val d = Seq(1, 2, 3)
    val e = d :+ b // assert: CollectionPromotionToAny

    val f = Seq(4, 5, 6)
    val g = d :+ f // assert: CollectionPromotionToAny

    val h = d :+ f // assert: CollectionPromotionToAny

    val i = collection.mutable.Buffer[Any]()
    i +: "hello" // scalafix: ok;

    val v = Vector(1, 2, 3)
    val v1 = 6
    val v2 = v :+ v1 // scalafix: ok;

    val l1 = List("A", "B", "cd")
    val l2 = "ef"
    val l3 = l1 :+ l2 // scalafix: ok;

    val l4 = List[Any](1, 2, 3)
    val l5 = "string"
    val l6 = l4 :+ l5 // scalafix: ok;

    val xs = Seq.empty[Any]
    println(xs :+ "hello") // scalafix: ok;

  }

}
