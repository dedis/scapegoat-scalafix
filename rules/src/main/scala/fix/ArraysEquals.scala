/*
rule = ArraysEquals
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ArraysEquals extends SemanticRule("ArraysEquals") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for comparison of arrays using == which will always return false.",
    pos,
    "Array equals is not an equality check. Use sameElements or convert to another collection type.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isArray(elem: Stat) = Util.matchType(elem, "scala/Array")

    doc.tree.collect {
      // Corresponds to a == b or a != b
      case t @ Term.ApplyInfix.After_4_6_0(lhs, Term.Name("==" | "!="), _, Term.ArgClause(List(rhs), _)) if isArray(lhs) && isArray(rhs) => Patch.lint(diag(t.pos))
      case _                                                                                                                             => Patch.empty
    }.asPatch
  }

}
