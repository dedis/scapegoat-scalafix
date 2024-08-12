/*
rule = FinalizerWithoutSuper
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._
import scala.meta.contrib.XtensionTreeOps

class FinalizerWithoutSuper extends SemanticRule("FinalizerWithoutSuper") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for overridden finalizers that do not call super.",
    pos,
    "Finalizers should call `super.finalize()` to ensure superclasses are able to run their finalization logic.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    // Note: finalize is deprecated since Java 9 and should not be used. However, some projects might still use Java 8 or older,
    // we thus chose to keep this rule.
    doc.tree.collect {
      // Corresponds to override def finalize { ... }
      case t @ Defn.Def.After_4_6_0(_, Term.Name("finalize"), _, _, body)
          // Checks if the body of the method does not contain super.finalize()
          if !body.exists {
            case Term.Apply.After_4_6_0(Term.Select(Term.Super(_, _), Term.Name("finalize")), Term.ArgClause(_, _)) => true
            case _                                                                                                  => false
          } => Patch.lint(diag(t.pos))
    }.asPatch
  }
}
