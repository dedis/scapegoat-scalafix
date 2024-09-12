/*
rule = ArraysToString
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ArraysToString extends SemanticRule("ArraysToString") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for explicit toString calls on arrays",
    pos,
    "Calling toString on an array does not perform a deep toString.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to a.toString or a.toString() or Array(1, 2, 3).toString or Array(1, 2, 3).toString()
      case t @ Term.Select(qual, Term.Name("toString")) if Util.matchType(qual, "scala/Array") => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
