/*
rule = AvoidSizeEqualsZero
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class AvoidSizeEqualsZero extends SemanticRule("AvoidSizeEqualsZero") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of Traversable.size and Traversable.length",
    pos,
    "Traversable.size or Traversable.length can be slow for some data structures, prefer Traversable.isEmpty, which is O(1).",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to Iterable(_).size or iterable.size
      case t @ Term.ApplyInfix.After_4_6_0(Term.Select(qual, Term.Name("size" | "length")), Term.Name("=="), _, Term.ArgClause(Lit.Int(0) :: Nil, _)) if Util.inheritsFrom(qual, "scala/collection/Iterable#") => Patch.lint(diag(t.pos))
      case _                                                                                                                                                                                                   => Patch.empty
    }.asPatch
  }
}
