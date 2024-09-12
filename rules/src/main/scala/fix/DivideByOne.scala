/*
rule = DivideByOne
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class DivideByOne extends SemanticRule("DivideByOne") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for division by one.",
    pos,
    "Divide by one will always return the original value.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.ApplyInfix.After_4_6_0(_, Term.Name("/" | "/="), _, Term.ArgClause(List(Lit.Int(1)), _)) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
