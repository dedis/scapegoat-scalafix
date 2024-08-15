/*
rule = RepeatedIfElseBody
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.collection.mutable
import scala.meta._

class RepeatedIfElseBody extends SemanticRule("RepeatedIfElseBody") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for the main branch and the else branch of an if being the same.",
    pos,
    "The if statement could be refactored if both branches are the same or start with the same instruction.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.If.After_4_4_0(_, Term.Block(ifStats), Term.Block(elseStats), _) =>
        if (ifStats.toString() == elseStats.toString()) Patch.lint(diag(t.pos)) // Blocks are the same
        else (ifStats, elseStats) match {
          case (headIf :: _, headElse :: _) if headIf.toString() == headElse.toString() => Patch.lint(diag(t.pos)) // First statement is the same
          case _                                                                        => Patch.empty
        }
      case t @ Term.If.After_4_4_0(_, ifStats, elseStats, _) if ifStats.toString() == elseStats.toString() => Patch.lint(diag(t.pos)) // If and else are the same, but not blocks e.g. if (a) 1 else 1
    }.asPatch
  }
}
