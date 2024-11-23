/*
rule = FindAndNotEqualsNoneReplaceWithExists
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

// Note: differs from Scapegoat rules since we also flag .find(...) == None which can simply be replaced by !.exists(...) which is more concise
class FindAndNotEqualsNoneReplaceWithExists extends SemanticRule("FindAndNotEqualsNoneReplaceWithExists") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks whether `find()` can be replaced with exists().",
    pos,
    "`find() != None` (resp find == None) can be replaced with `exists()` (resp !exists()), which is more concise.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.ApplyInfix.After_4_6_0(Term.Apply.After_4_6_0(Term.Select(_, Term.Name("find")), _), Term.Name("==" | "!="), _, Term.ArgClause(List(Term.Name("None")), _)) => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
