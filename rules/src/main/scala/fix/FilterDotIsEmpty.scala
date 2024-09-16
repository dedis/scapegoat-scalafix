/*
rule = FilterDotIsEmpty
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class FilterDotIsEmpty extends SemanticRule("FilterDotIsEmpty") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of filter().isEmpty.",
    pos,
    "`filter()` scans the entire collection, which can potentially be avoided if the element exists in the collection - `filter().isEmpty` can be replaced with `!exists()`.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(Term.Apply.After_4_6_0(Term.Select(_, Term.Name("filter")), _), Term.Name("isEmpty")) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
