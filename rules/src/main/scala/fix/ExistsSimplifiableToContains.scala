/*
rule = ExistsSimplifiableToContains
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ExistsSimplifiableToContains extends SemanticRule("ExistsSimplifiableToContains") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks if `exists()` can be simplified to `contains()`",
    pos,
    "`exists(x => x == y)` can be replaced with `contains(y)`.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    // Array, seq, list, set, map, vector, range, varargs implement the contains method
    // Ranges are respectively for until and to
    def isContainsTraversable(term: Term) = Util.isArray(term) || Util.isSeq(term) || Util.isList(term) || Util.isSet(term) || Util.isMap(term) || Util.isVector(term) || Util.matchType(term, "scala/collection/immutable/Range", "scala/collection/immutable/Range.Inclusive") || Util.isVarArgs(term)
    def isUsedOnce(other: Tree, vari: String) = !other.collect { case Term.Name(n) => n == vari }.contains(true)

    // Handle normal function (with variable)
    def ruleNotAnonymous(var1: String, lhs: Term, rhs: Term.ArgClause, t: Term): Patch = {
      // We check if one of the two sides is the variable, and if it is used only once i.e. not in the other side, as otherwise contains is not sufficient
      (lhs, rhs) match {
        case (Term.Name(var2), other) if var1 == var2 && isUsedOnce(other, var2)                          => Patch.lint(diag(t.pos))
        case (other, Term.ArgClause(List(Term.Name(var2)), _)) if var1 == var2 && isUsedOnce(other, var2) => Patch.lint(diag(t.pos))
        // Ignore calls to _.1 or ._2 for maps as contains would not be sufficient
        case (Term.Select(Term.Name(_), Term.Name("_1" | "_2")), _)                                                       => Patch.empty
        case (Term.Select(Term.Name(var2), _), other) if var1 == var2 && isUsedOnce(other, var2)                          => Patch.lint(diag(t.pos))
        case (other, Term.ArgClause(List(Term.Select(Term.Name(var2), _)), _)) if var1 == var2 && isUsedOnce(other, var2) => Patch.lint(diag(t.pos))
        case _                                                                                                            => Patch.empty
      }
    }

    // Handle anonymous function (with placeholders)
    def ruleAnonymous(lhs: Term, rhs: Term.ArgClause, t: Term): Patch = {
      (lhs, rhs) match {
        // Here the placeholder is necessarily used one, no need for the check, we simply check for _.1 or _.2 for maps
        case (Term.Select(Term.Placeholder(), Term.Name("_1" | "_2")), _)                          => Patch.empty
        case (Term.Placeholder() | Term.Select(Term.Placeholder(), _), _)                          => Patch.lint(diag(t.pos))
        case (_, Term.ArgClause(List(Term.Placeholder() | Term.Select(Term.Placeholder(), _)), _)) => Patch.lint(diag(t.pos))
        case _                                                                                     => Patch.empty
      }
    }

    doc.tree.collect {
      // Corresponds to l.exists(x => x == y), l.exists(x => y == x), l.exists(x => x != y), l.exists(x => y != x)
      case t @ Term.Apply.After_4_6_0(Term.Select(qual, Term.Name("exists")), Term.ArgClause(List(fun), _)) if isContainsTraversable(qual) =>
        // Extract block in case of .exists {} instead of .exists()
        val extractedFun = fun match {
          case Term.Block(List(stats)) => stats
          case _                       => fun
        }
        // Avoids code repetition
        extractedFun match {
          case Term.Function.After_4_6_0(Term.ParamClause(List(Term.Param(_, Term.Name(var1), _, _)), _), Term.ApplyInfix.After_4_6_0(lhs, Term.Name("==" | "!="), _, rhs)) => ruleNotAnonymous(var1, lhs, rhs, t)
          case Term.AnonymousFunction(Term.ApplyInfix.After_4_6_0(lhs, Term.Name("==" | "!="), _, rhs))                                                                     => ruleAnonymous(lhs, rhs, t)
          case _                                                                                                                                                            => Patch.empty
        }
    }.asPatch
  }

}
