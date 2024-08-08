/*
rule = CatchExceptionImmediatelyRethrown
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class CatchExceptionImmediatelyRethrown extends SemanticRule("CatchExceptionImmediatelyRethrown") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for try-catch blocks that immediately rethrow caught exceptions.",
    pos,
    "Immediately re-throwing a caught exception is equivalent to not catching it at all.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to try { ... } catch { case e: Exception => throw e }, may have multiple catches
      case Term.Try(_, catches, _) => catches.collect {
          case c @ Case(Pat.Typed(Pat.Var(Term.Name(a)), _), _, Term.Throw(Term.Name(b))) if a == b => Patch.lint(diag(c.pos))
          case _                                                                                    => Patch.empty
        }
    }.flatten.asPatch
  }
}
