/*
rule = LooksLikeInterpolatedString
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class LooksLikeInterpolatedString extends SemanticRule("LooksLikeInterpolatedString") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for strings that look like they should be interpolated.",
    pos,
    "Did you forget to prefix this string with an s, f or raw to interpolate it?",
    LintSeverity.Warning
  )

  private val regexes = List("\\$\\{[a-z][.a-zA-Z0-9_]*\\}".r, "\\$[a-z][.a-zA-Z0-9_]*".r)

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isInterpolate(arg: Tree): Boolean = arg match {
      case Term.Interpolate(_, _, _) => true
      case _                         => false
    }

    doc.tree.collect {
      case Term.Interpolate(_, _, _) => Patch.empty // explicitly ignore interpolated strings
      /* Corresponds to string with possible interpolation syntax e.g. "Hello ${name}" or "Hello $name".
      We first check if the regex matches.
      Then, or condition here (t.parent.isEmpty || !isInterpolate(t.parent.get)) is to avoid flagging interpolated strings
      We first check if there is no parent (i.e. it is a lonely string) and if it has one, we check if it is not an interpolation
      Because it could simply be a string contained in a Term.Interpolate. This also helps flagging escaped $ in strings */
      case t @ Lit.String(str) if regexes.exists(r => r.findFirstIn(str).nonEmpty) && (t.parent.isEmpty || !isInterpolate(t.parent.get)) => Patch.lint(diag(t.pos))

    }.asPatch
  }
}
