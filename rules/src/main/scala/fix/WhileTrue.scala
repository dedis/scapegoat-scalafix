/*
rule = WhileTrue
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class WhileTrue extends SemanticRule("WhileTrue") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for code that uses a while(true) block.",
    pos,
    "A while true loop is unlikely to be meant for production.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case w @ Term.While(Lit.Boolean(true), _) => Patch.lint(diag(w.pos))
      // This is only for compatibility with Scala version < 3. Do-while support has been dropped in Scala 3
      case d @ Term.Do(_, Lit.Boolean(true)) => Patch.lint(diag(d.pos))
    }.asPatch
  }
}
