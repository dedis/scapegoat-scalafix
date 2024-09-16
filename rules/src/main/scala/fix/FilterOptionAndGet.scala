/*
rule = FilterOptionAndGet
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class FilterOptionAndGet extends SemanticRule("FilterOptionAndGet") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks whether the expression can be rewritten using flatten.",
    pos,
    "`filter(_.isDefined).map(_.get)` can be replaced with `flatten`.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def extractFun(term: Term): Stat = term match {
      case Term.Block(List(fun)) => fun
      case t                     => t
    }

    def isFunction(term: Stat, functionName: String): Boolean = term match {
      case Term.Function.After_4_6_0(Term.ParamClause(List(Term.Param(_, Term.Name(var1), _, _)), _), Term.Select(Term.Name(var2), Term.Name(funName))) if funName == functionName && var1 == var2 => true
      case Term.AnonymousFunction(Term.Select(_, Term.Name(funName))) if funName == functionName                                                                                                   => true
      case _                                                                                                                                                                                       => false
    }

    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(Term.Select(Term.Apply.After_4_6_0(Term.Select(_, Term.Name("filter")), Term.ArgClause(List(isDefinedFun), _)), Term.Name("map")), Term.ArgClause(List(mapFun), _))
          if isFunction(extractFun(isDefinedFun), "isDefined") && isFunction(extractFun(mapFun), "get") => Patch.lint(diag(t.pos))
    }.asPatch
  }

}
