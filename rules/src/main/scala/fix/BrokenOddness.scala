/*
rule = BrokenOddness
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class BrokenOddness extends SemanticRule("BrokenOddness") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for potentially broken odd checks.",
    pos,
    "Code that attempts to check for oddness using 'x % 2 == 1' will fail on negative numbers. Consider using 'x % 2 != 0'.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to a % 2 == 1
      case t @ Term.ApplyInfix.After_4_6_0(Term.ApplyInfix.After_4_6_0(_, Term.Name("%"), _, Term.ArgClause(List(Lit.Int(2)), _)), Term.Name("=="), _, Term.ArgClause(List(Lit.Int(1)), _)) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
