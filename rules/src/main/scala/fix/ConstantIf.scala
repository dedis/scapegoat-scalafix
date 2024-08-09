/*
rule = ConstantIf
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ConstantIf extends SemanticRule("ConstantIf") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for code where the if condition compiles to a constant.",
    pos,
    "An if condition which gets compiled to a constant, like e.g. if (1 < 2) or if (false) doesn't add any value and should be avoided.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.If.After_4_4_0(
            // Corresponds to if(true) / if(false) OR (| symbol) if(1 < 2) / if(2 < 1) or any comparison between literals which is necessarily constant.
            Lit.Boolean(_) | Term.ApplyInfix.After_4_6_0(Lit(_), _, _, Term.ArgClause(List(Lit(_)), _)),
            _,
            _,
            _
          ) => Patch.lint(diag(t.pos))
    }.asPatch
  }
}
