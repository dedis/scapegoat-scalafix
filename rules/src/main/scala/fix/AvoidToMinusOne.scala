/*
rule = AvoidToMinusOne
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class AvoidToMinusOne extends SemanticRule("AvoidToMinusOne") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for ranges using (j to k - 1).",
    pos,
    "A range in the following format (j to k - 1) can be simplified to (j until k).",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to j to k - 1. The first operator (j) is not important, only the second part, i.e. to k - 1 is.
      case t @ Term.ApplyInfix.After_4_6_0(_, Term.Name("to"), _, Term.ArgClause(List(Term.ApplyInfix.After_4_6_0(_, Term.Name("-"), _, Term.ArgClause(List(Lit(1)), _))), _)) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
