/*
rule = PointlessTypeBounds
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class PointlessTypeBounds extends SemanticRule("PointlessTypeBounds") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Finds type bounds of the form `A <: Any` or `A >: Nothing`.",
    pos,
    "Type bound resolves to `Nothing <: T <: Any`. Did you mean to put in other bounds?",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to type parameters with pointless bounds like [A >: Nothing] or [A <: Any]
      case t @ (Type.Bounds(Some(Type.Name("Nothing")), _) | Type.Bounds(_, Some(Type.Name("Any")))) => Patch.lint(diag(t.pos))
      case _                                                                                         => Patch.empty
    }.asPatch
  }

}
