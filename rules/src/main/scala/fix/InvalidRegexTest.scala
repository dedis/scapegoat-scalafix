/*
rule = InvalidRegexTest
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import java.util.regex.PatternSyntaxException
import scala.annotation.nowarn
import scala.meta._

class InvalidRegexTest extends SemanticRule("InvalidRegexTest") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for invalid regex literals.",
    pos,
    "Invalid regex literals can fail at compile time with a PatternSyntaxException. This could be caused by e.g. dangling meta characters, or unclosed escape characters, etc.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    // Put in a function to avoid returning in pattern match, causing warn
    def tryCompile(regex: String): Boolean = {
      try regex.r
      catch {
        case _: PatternSyntaxException => return false
      }
      true
    }

    doc.tree.collect {
      case t @ Term.Select(Lit.String(regex), Term.Name("r")) if !tryCompile(regex) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
