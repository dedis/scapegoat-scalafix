/*
rule = FilterDotHead
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class FilterDotHead extends SemanticRule("FilterDotHead") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of filter().head.",
    pos,
    "`filter().head` can throw an exception if the collection is empty - it can be replaced with `find() match {...}`.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(Term.Apply.After_4_6_0(Term.Select(_, Term.Name("filter")), _), Term.Name("head")) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
