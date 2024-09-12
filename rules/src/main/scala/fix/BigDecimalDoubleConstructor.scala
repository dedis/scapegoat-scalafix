/*
rule = BigDecimalDoubleConstructor
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class BigDecimalDoubleConstructor extends SemanticRule("BigDecimalDoubleConstructor") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of BigDecimal(double) which can be unsafe.",
    pos,
    "The results of this constructor can be somewhat unpredictable. E.g. writing new BigDecimal(0.1) in Java creates a BigDecimal which is actually equal to 0.1000000000000000055511151231257827021181583404541015625. This is because 0.1 cannot be represented exactly as a double.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isFloatOrDouble(arg: Stat) = arg match {
      case Lit.Float(_) | Lit.Double(_) => true
      case _                            => Util.matchType(arg, "scala/Double") || Util.matchType(arg, "scala/Float")
    }

    doc.tree.collect {
      // Corresponds to BigDecimal(...) where ... is a floating point / double value.
      case t @ Term.Apply.After_4_6_0(Term.Name("BigDecimal"), Term.ArgClause(List(arg), _)) if isFloatOrDouble(arg)                                                                    => Patch.lint(diag(t.pos))
      case t @ Init.After_4_6_0(Type.Select(Term.Select(Term.Name("java"), Term.Name("math")), Type.Name("BigDecimal")), _, List(Term.ArgClause(List(arg), _))) if isFloatOrDouble(arg) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
