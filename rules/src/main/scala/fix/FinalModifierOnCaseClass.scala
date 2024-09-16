/*
rule = FinalModifierOnCaseClass
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class FinalModifierOnCaseClass extends SemanticRule("FinalModifierOnCaseClass") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for case classes without a final modifier.",
    pos,
    "Using case classes without final modifier can lead to surprising breakage.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Class.After_4_6_0(mods, _, _, _, _) if mods.exists(_.is[Mod.Case]) && !mods.exists(_.is[Mod.Abstract]) && !mods.exists(_.is[Mod.Final]) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
