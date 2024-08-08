/*
rule = CatchFatal
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class CatchFatal extends SemanticRule("CatchFatal") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for try blocks that catch fatal exceptions: VirtualMachineError, ThreadDeath, InterruptedException, LinkageError, ControlThrowable.",
    pos,
    "Did you intend to catch a fatal exception? Consider using scala.util.control.NonFatal instead.",
    LintSeverity.Error
  )

  private val fatalExceptions = Set("VirtualMachineError", "ThreadDeath", "InterruptedException", "LinkageError", "ControlThrowable")

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to try { ... } catch { case e: NullPointerException => ... }, may have multiple catches
      case Term.Try(_, catches, _) => catches.collect {
          case Case(pat, _, _) => pat match {
              case Pat.Typed(_, Type.Name(exception)) if fatalExceptions.contains(exception) => Patch.lint(diag(pat.pos))
              case _                                                                         => Patch.empty
            }
        }
    }
  }.flatten.asPatch
}
