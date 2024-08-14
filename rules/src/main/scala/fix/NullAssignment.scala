/*
rule = NullAssignment
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class NullAssignment extends SemanticRule("NullAssignment") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of null in assignments.",
    pos,
    "Use an Option instead when the value can be empty.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Val(_, _, _, Lit.Null())                => Patch.lint(diag(t.pos)) // val a = null
      case t @ Defn.Var.After_4_7_2(_, _, _, Lit.Null())    => Patch.lint(diag(t.pos)) // var b = null
      case t @ Defn.Def.After_4_6_0(_, _, _, _, Lit.Null()) => Patch.lint(diag(t.pos)) // def c = null
      case t @ Term.Assign(_, Lit.Null())                   => Patch.lint(diag(t.pos)) // a = null
    }.asPatch
  }
}
