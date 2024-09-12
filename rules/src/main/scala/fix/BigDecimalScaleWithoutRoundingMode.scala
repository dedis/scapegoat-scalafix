/*
rule = BigDecimalScaleWithoutRoundingMode
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class BigDecimalScaleWithoutRoundingMode extends SemanticRule("BigDecimalScaleWithoutRoundingMode") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of 'setScale()' on a BigDecimal without setting the rounding mode can throw an exception.",
    pos,
    "When using 'setScale()' on a BigDecimal without setting the rounding mode, this can throw an exception if rounding is required. Did you mean to call 'setScale(s, RoundingMode.XYZ)'?",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isBigDecimal(arg: Stat) = Util.matchType(arg, "scala/math/BigDecimal") || Util.matchType(arg, "java/math/BigDecimal")

    doc.tree.collect {
      // Corresponds to b.setScale(2) where b is a BigDecimal or BigDecimal(2).setScale(2), i.e. there is only one argument passed.
      // Also corresponds to new java.math.BigDecimal(2).setScale(2) or b.setScale(2) where b is a java.math.BigDecimal
      case t @ Term.Apply.After_4_6_0(Term.Select(_, Term.Name("setScale")), Term.ArgClause(List(_), _)) if isBigDecimal(t) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
