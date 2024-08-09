/*
rule = ComparisonWithSelf
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ComparisonWithSelf extends SemanticRule("ComparisonWithSelf") {

  def diag(pos: Position) = Diagnostic("", "Checks for equality checks with itself.", pos, "Comparison with self will always yield true.", LintSeverity.Warning)

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to a == a, b != b
      case t @ Term.ApplyInfix.After_4_6_0(lhs, Term.Name("==") | Term.Name("!="), _, Term.ArgClause(List(rhs), _)) if lhs.toString() == rhs.toString() => Patch.lint(diag(t.pos))
      // Corresponds to a.equals(a)
      case t @ Term.Apply.After_4_6_0(Term.Select(lhs, Term.Name("equals")), Term.ArgClause(List(rhs), _)) if lhs.toString() == rhs.toString() => Patch.lint(diag(t.pos))
    }.asPatch
  }
}
