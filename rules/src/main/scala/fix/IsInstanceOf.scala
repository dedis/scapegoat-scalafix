/*
rule = IsInstanceOf
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class IsInstanceOf extends SemanticRule("IsInstanceOf") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of isInstanceOf.",
    pos,
    "Use of isInstanceOf is considered a bad practice - consider using pattern matching instead.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to a.asInstanceOf(...) or a.asInstanceOf[...]
      case t @ Term.Select(_, Term.Name("isInstanceOf")) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
