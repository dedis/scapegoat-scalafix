/*
rule = EmptySynchronized
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class EmptySynchronized extends SemanticRule("EmptySynchronized") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for empty synchronized blocks.",
    pos,
    "An empty synchronized block is considered as dead code.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    doc.tree.collect {
      // Corresponds respectively to synchronized (()) and synchronized { } and synchronized { () }
      case t @ Term.Apply.After_4_6_0(Term.Name("synchronized"), Term.ArgClause(List(Lit.Unit()) | List(Term.Block(Nil | List(Lit.Unit()))), _)) => Patch.lint(diag(t.pos))
    }.asPatch
  }
}
