/*
rule = FilterDotHeadOption
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class FilterDotHeadOption extends SemanticRule("FilterDotHeadOption") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of filter().headOption.",
    pos,
    "`filter()` scans the entire collection, which is unnecessary if you only want to get the first element that satisfies the predicate - `filter().headOption` can be replaced with `find()` to potentially avoid scanning the entire collection.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(Term.Apply.After_4_6_0(Term.Select(_, Term.Name("filter")), _), Term.Name("headOption")) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
