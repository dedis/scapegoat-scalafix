/*
rule = BoundedByFinalType
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class BoundedByFinalType extends SemanticRule("BoundedByFinalType") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for types with upper bounds of a final type.",
    pos,
    "Pointless type bound. Type parameter can only be a single value.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to [A <: ...] in class Test[A <: String] or def foo[B <: Integer] = {}
      case Type.ParamClause(List(params)) =>
        params.collect {
          // String is seen as Predef.String, not java/lang/String. String is final but not Predef.String, even though they are essentially the same.
          case p @ Type.Param.After_4_6_0(_, _, _, Type.Bounds(_, hi), _, _) if hi.exists(h => h.symbol.info.exists(i => i.isFinal) || h.toString == "String") => Patch.lint(diag(p.pos))
        }
    }.flatten.asPatch
  }

}
