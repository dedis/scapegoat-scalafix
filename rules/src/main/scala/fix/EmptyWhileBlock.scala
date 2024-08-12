/*
rule = EmptyWhileBlock
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class EmptyWhileBlock extends SemanticRule("EmptyWhileBlock") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for empty try blocks.",
    pos,
    "An empty try block is considered as dead code.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    doc.tree.collect {
      // Corresponds to while (cond) (), while (cond) { } and while (cond) { () }
      case t @ Term.While(_, Lit.Unit() | Term.Block(Nil | List(Lit.Unit()))) => Patch.lint(diag(t.pos))
      case _                                                                  => Patch.empty
    }.asPatch
  }
}
