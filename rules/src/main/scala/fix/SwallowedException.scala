/*
rule = SwallowedException
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class SwallowedException extends SemanticRule("SwallowedException") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Finds catch blocks that don't handle caught exceptions.",
    pos,
    "If you use a try/catch block to deal with an exception, you should handle all of the caught exceptions or name it ignore(d). If you're throwing another exception in the result, you should include the original exception as the cause.",
    LintSeverity.Warning
  )

  private def containsCorrectRethrow(stats: List[Stat], name: String): Boolean = {
    // This first finds the throw statement, then checks if the values in the new thrown exception contain the original exception
    stats.exists {
      // For some reason in Scalameta, throws can have multiple argclauses (e.g. throw new Exception("a")("b") i.e. exception currying)
      // In practice, we choose to ignore this case as this is really uncommon. If that becomes an issue, a fix is quite easy to implement.
      case Term.Throw(Term.New(Init.After_4_6_0(_, _, List(Term.ArgClause(values, _))))) => // Find throw statement
        values.exists {
          case Term.Name(otherName) if otherName == name => true // Check if the exception is rethrown
          case _                                         => false
        }
      case _ => false
    }
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Try(_, catches, _) =>
        catches.collect {
          // Pat.Typed(Pat.Var(Term.Name(e)), Excep) roughly corresponds to case e: Excep
          case Case(Pat.Typed(Pat.Var(Term.Name("ignore" | "ignored")), _), _, _)                                                   => Patch.empty // Ignore cases where the exception is named ignore(d)
          case c @ Case(_, _, Term.Block(Nil) | Lit.Unit())                                                                         => Patch.lint(diag(c.pos)) // Empty catch block
          case c @ Case(Pat.Typed(Pat.Var(Term.Name(name)), _), _, Term.Block(stats)) if !containsCorrectRethrow(stats, name)       => Patch.lint(diag(c.pos)) // If the catch block contains a throw that doesn't include the caught exception
          case c @ Case(Pat.Typed(Pat.Var(Term.Name(name)), _), _, thr @ Term.Throw(_)) if !containsCorrectRethrow(List(thr), name) => Patch.lint(diag(c.pos)) // Same, but with direct throw statement instead of block
          case _                                                                                                                    => Patch.empty
        }
    }.flatten.asPatch
  }
}
