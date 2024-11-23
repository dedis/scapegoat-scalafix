/*
rule = FilterDotSize
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class FilterDotSize extends SemanticRule("FilterDotSize") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of filter().size.",
    pos,
    "`filter().size` can be replaced with `count()`, which is more concise.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(Term.Apply.After_4_6_0(Term.Select(_, Term.Name("filter")), _), Term.Name("size")) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
