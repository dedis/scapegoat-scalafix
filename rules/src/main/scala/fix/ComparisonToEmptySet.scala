/*
rule = ComparisonToEmptySet
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ComparisonToEmptySet extends SemanticRule("ComparisonToEmptySet") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for code like `a == Set()` or `a == Set.empty`.",
    pos,
    "Prefer use of `isEmpty` instead of comparison to an empty Set.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isEmptySet(term: Term) = term match {
      // Set()
      case Term.Apply.After_4_6_0(Term.Name("Set"), Term.ArgClause(Nil, _)) => true
      // Set.apply()
      case Term.Apply.After_4_6_0(Term.Select(Term.Name("Set"), Term.Name("apply")), Term.ArgClause(Nil, _)) => true
      // Set.empty
      case Term.Select(Term.Name("Set"), Term.Name("empty")) => true
      case _                                                 => false
    }

    doc.tree.collect {
      // Corresponds to a == Set() or Set() == a, or any of the empty sets above. Also handles a != Set() or Set() != a, with the empty sets above too.
      case Term.ApplyInfix.After_4_6_0(lhs, Term.Name("==" | "!="), _, Term.ArgClause(List(rhs), _)) if isEmptySet(lhs) || isEmptySet(rhs) => Patch.lint(diag(lhs.pos))
    }.asPatch
  }

}
