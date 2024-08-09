/*
rule = CatchThrowable
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class CatchThrowable extends SemanticRule("CatchThrowable") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for try blocks that catch Throwable..",
    pos,
    "Did you intend to catch all throwables? Consider catching a more specific exception class.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to try { ... } catch { case e: Throwable => ... }, may have multiple catches
      case Term.Try(_, catches, _) => catches.collect {
          case Case(pat, _, _) => pat match {
              case Pat.Typed(_, Type.Name("Throwable")) => Patch.lint(diag(pat.pos))
              case _                                    => Patch.empty
            }
        }
    }
  }.flatten.asPatch
}
