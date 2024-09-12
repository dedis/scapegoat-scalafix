/*
rule = ClassNames
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ClassNames extends SemanticRule("ClassNames") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Ensures class names adhere to the style guidelines.",
    pos,
    "Class names should begin with uppercase letter and not contain underscores.",
    LintSeverity.Info
  )

  private val regex = "^[A-Z][A-Za-z0-9]*$".r // Regex to match class names that start with an uppercase letter and contain no underscores

  override def fix(implicit doc: SemanticDocument): Patch = {

    doc.tree.collect {
      // Corresponds to class aClass or class My_Class
      case c @ Defn.Class.After_4_6_0(_, Type.Name(name), _, _, _) if !regex.matches(name) => Patch.lint(diag(c.pos))
    }.asPatch
  }

}
