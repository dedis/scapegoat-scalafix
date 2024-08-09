/*
rule = EmptyFor
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class EmptyFor extends SemanticRule("EmptyFor") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for empty for loops.",
    pos,
    "An empty for loop isn't a common practice and in most cases is considered as dead code.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to for ( loop condition ) { }
      case t @ Term.For(_, Term.Block(Nil | List(Lit.Unit()))) => Patch.lint(diag(t.pos))
    }.asPatch
  }
}
