/*
rule = EmptyTryBlock
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class EmptyTryBlock extends SemanticRule("EmptyTryBlock") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for empty try blocks.",
    pos,
    "An empty try block is considered as dead code.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    doc.tree.collect {
      // Corresponds to try (()), try { } and try { () }
      case t @ Term.Try(Lit.Unit() | Term.Block(Nil | List(Lit.Unit())), _, _) => Patch.lint(diag(t.pos))
      case _                                                                   => Patch.empty
    }.asPatch
  }
}
