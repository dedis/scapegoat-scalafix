/*
rule = AsInstanceOf
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class AsInstanceOf extends SemanticRule("AsInstanceOf") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of asInstanceOf.",
    pos,
    "Use of asInstanceOf is considered a bad practice - consider using pattern matching instead.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to a.asInstanceOf(...) or a.asInstanceOf[...]
      case t @ Term.Select(_, Term.Name("asInstanceOf")) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
