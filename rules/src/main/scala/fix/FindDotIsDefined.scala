/*
rule = FindDotIsDefined
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class FindDotIsDefined extends SemanticRule("FindDotIsDefined") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks whether `find()` can be replaced with `exists()`.",
    pos,
    "`find().isDefined` can be replaced with `exists()`, which is more concise.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(Term.Apply.After_4_6_0(Term.Select(_, Term.Name("find")), _), Term.Name("isDefined")) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
