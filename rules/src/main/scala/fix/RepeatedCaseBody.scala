/*
rule = RepeatedCaseBody
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.collection.mutable
import scala.meta._

class RepeatedCaseBody extends SemanticRule("RepeatedCaseBody") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for case statements which have the same body.",
    pos,
    "Case body is repeated. Consider merging pattern clauses together.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    val bodyMap = mutable.HashMap[Tree, mutable.HashSet[String]]() // Map with parent as key and bodies as values.
    // Keeping the parents make our pattern matching logic agnostic to the parent type and thus handles all cases where
    // a case statement is present (even possible future ones). This stems from the fact that cases can be used in
    // match, partial functions and exceptions for now.
    val flaggedCases = mutable.HashSet[Case]()
    doc.tree.traverse {
      case t @ Case(_, _, Term.Block(stats)) if stats.size >= 4 && t.parent.isDefined => // last condition should be guaranteed, but we check to avoid error on the next line
        // In our version, we ignore the guard and only check the body, as guard or not, a body should not be replicated
        val bodies = bodyMap.getOrElseUpdate(t.parent.get, mutable.HashSet[String]()) // Start building the case set
        val statsString = stats.toString() // We use the string representation of the blocks for easier comparison
        // (otherwise we would have to compare trees and that is a lot more complex)
        if (bodies.contains(statsString)) flaggedCases.add(t) // If the body is already present, flag it
        else bodies.add(statsString) // Otherwise add it to the set
    }
    flaggedCases.map(t => Patch.lint(diag(t.pos))).asPatch // Return all flagged cases as patches
  }
}
