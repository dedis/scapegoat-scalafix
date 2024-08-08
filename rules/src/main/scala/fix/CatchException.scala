/*
rule = CatchException
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class CatchException extends SemanticRule("CatchException") {

  private def diag(pos: Position) = Diagnostic("", "Catch exception", pos, "Did you intend to catch all exceptions? Consider catching a more specific exception class.", LintSeverity.Warning)

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to try { ... } catch { case e: Exception => ... }, may have multiple catches
      case Term.Try(_, catches, _) => catches.collect {
          case Case(pat, _, _) => pat match {
              case Pat.Typed(_, Type.Name("Exception")) => Patch.lint(diag(pat.pos))
              case _                                    => Patch.empty
            }
        }
    }
  }.flatten.asPatch
}
