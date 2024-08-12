/*
rule = EmptyMethod
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class EmptyMethod extends SemanticRule("EmptyMethod") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for empty method statements.",
    pos,
    "An empty method is considered as dead code.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    /* We only look at private definitions since they cannot be overriden (except in the same class), so they are deadcode.
    Vs public methods which can be overriden and are not deadcode. */

    doc.tree.collect {
      // Corresponds to private def name = { } or private def name = { () } or private def name = ()
      case t @ Defn.Def.After_4_6_0(mods, _, _, _, Term.Block(Nil | List(Lit.Unit())) | Lit.Unit())
          if mods.exists(m => m.toString() == "private") => Patch.lint(diag(t.pos))
      case _ => Patch.empty
    }.asPatch
  }
}
