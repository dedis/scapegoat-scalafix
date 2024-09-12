/*
rule = DoubleNegation
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class DoubleNegation extends SemanticRule("DoubleNegation") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for code like !(!b).",
    pos,
    "Double negation can be removed, e.g. !(!b) it equal to just b.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.ApplyUnary(Term.Name("!"), Term.ApplyUnary(Term.Name("!"), _)) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
