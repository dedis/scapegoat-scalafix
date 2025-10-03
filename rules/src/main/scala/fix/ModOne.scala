/*
rule = ModOne
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ModOne extends SemanticRule("ModOne") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for `x % 1` which will always return `0`",
    pos,
    "Any expression x % 1 will always return 0.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds x % 1
      case t @ Term.ApplyInfix.After_4_6_0(_, Term.Name("%"), _, Term.ArgClause(List(Lit.Int(1)), _)) => Patch.lint(diag(t.pos))
      case _                                                                                          => Patch.empty
    }.asPatch
  }

}
