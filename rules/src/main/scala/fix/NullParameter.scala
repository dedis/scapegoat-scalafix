/*
rule = NullParameter
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class NullParameter extends SemanticRule("NullParameter") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of null in method invocation",
    pos,
    "Use an Option instead when the value can be empty and pass down a None instead.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.ArgClause(values, _) => values.collect {
          case Lit.Null() => Patch.lint(diag(values.head.pos))
          case _          => Patch.empty
        }
    }.flatten.asPatch
  }
}
