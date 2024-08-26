/*
rule = InterpolationToString
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class InterpolationToString extends SemanticRule("InterpolationToString") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for string interpolations that have .toString in their arguments",
    pos,
    "A call to .toString is not necessary, since arguments in format strings will be replaced by the value of a toString call on them.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Interpolate(_, _, args) =>
        args.collect {
          // Corresponds to ${x.toString()} or ${x.toString} arguments in string interpolations
          case t @ Term.Block(List(Term.Apply.After_4_6_0(Term.Select(_, Term.Name("toString")), _) | Term.Select(_, Term.Name("toString")))) => Patch.lint(diag(t.pos))
          case _                                                                                                                              => Patch.empty
        }
      case _ => List(Patch.empty)
    }.flatten.asPatch
  }
}
