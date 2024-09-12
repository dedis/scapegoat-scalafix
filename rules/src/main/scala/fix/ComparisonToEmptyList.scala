/*
rule = ComparisonToEmptyList
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ComparisonToEmptyList extends SemanticRule("ComparisonToEmptyList") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for code like `a == List()`, `a == Nil`, `a != List()` or `a != Nil`.",
    pos,
    "Prefer use of `isEmpty`, or `nonEmpty` instead of comparison to an empty List.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isEmptyList(term: Term) = term match {
      // List()
      case Term.Apply.After_4_6_0(Term.Name("List"), Term.ArgClause(Nil, _)) => true
      // List.apply()
      case Term.Apply.After_4_6_0(Term.Select(Term.Name("List"), Term.Name("apply")), Term.ArgClause(Nil, _)) => true
      // List.empty
      case Term.Select(Term.Name("List"), Term.Name("empty")) => true
      // Nil
      case Term.Name("Nil") => true
      case _                => false
    }

    doc.tree.collect {
      // Corresponds to a == List() or List() == a, or any of the empty lists above. Also handles a != List() or List() != a, with the empty lists above too.
      case Term.ApplyInfix.After_4_6_0(lhs, Term.Name("==" | "!="), _, Term.ArgClause(List(rhs), _)) if isEmptyList(lhs) || isEmptyList(rhs) => Patch.lint(diag(lhs.pos))
    }.asPatch
  }

}
