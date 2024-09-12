/*
rule = CollectionNamingConfusion
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class CollectionNamingConfusion extends SemanticRule("CollectionNamingConfusion") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for variables that are confusingly named.",
    pos,
    "E.g. an instance of a Set is confusingly referred to by a variable called/containing list, or the other way around.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def leaveOneOut(name: String): Set[String] = name match {
      case "list"   => Set("set", "vector", "seq", "array", "map")
      case "set"    => Set("list", "vector", "seq", "array", "map")
      case "vector" => Set("list", "set", "seq", "array", "map")
      case "seq"    => Set("list", "set", "vector", "array", "map")
      case "array"  => Set("list", "set", "vector", "seq", "map")
      case "map"    => Set("list", "set", "vector", "seq", "array")
      case _        => Set.empty
    }

    doc.tree.collect {
      // Corresponds to vals / vars of collection types that have a confusing name (i.e. of another collection) e.g. val set = List(1,2,3) or var listSet =
      case t @ Defn.Val(_, List(Pat.Var(Term.Name(qual))), _, Term.Apply.After_4_6_0(Term.Name(collName), _)) if leaveOneOut(collName.toLowerCase()).exists(qual.contains)             => Patch.lint(diag(t.pos))
      case t @ Defn.Var.After_4_7_2(_, List(Pat.Var(Term.Name(qual))), _, Term.Apply.After_4_6_0(Term.Name(collName), _)) if leaveOneOut(collName.toLowerCase()).exists(qual.contains) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
