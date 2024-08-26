/*
rule = VariableShadowing
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.annotation.nowarn
import scala.collection.mutable
import scala.meta._

class VariableShadowing extends SemanticRule("VariableShadowing") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for multiple uses of the variable name in nested scopes.",
    pos,
    "Variable shadowing is very useful, but can easily lead to nasty bugs in your code. Shadowed variables can be potentially confusing to other maintainers when the same name is adopted to have a new meaning in a nested scope.",
    LintSeverity.Warning
  )
  override def fix(implicit doc: SemanticDocument): Patch = {

    def collect(block: List[Stat], vars: mutable.HashSet[String]): List[Patch] = {
      // Extracts the stats from blocks and otherwise converts them so that we can do a foreach on them
      def normalize(term: Stat): List[Stat] = term match {
        case Term.Block(stats) => stats
        case _                 => List(term)
      }

      @nowarn // this is because in Scala 2, t.children at line 47 is a List[Tree], but in Scala 3 it is a List[Stat], so we need to suppress the warning for the unreachable case case _ in Scala 3
      def collectInner(block: List[Stat], vars: mutable.HashSet[String], flagged: mutable.HashSet[Position]): Unit = {

        def handleCases(c: List[Case], vars: mutable.HashSet[String]): Unit = {
          c.foreach {
            // Collect variables in cases e.g. case a => ...
            case Case(Pat.Var(name), _, body) => updateVars(name.value, name.pos); collectInner(normalize(body), vars.clone(), flagged)
            case _                            => ()
          }
        }

        def updateVars(name: String, pos: Position): Unit = {
          if (vars.contains(name)) flagged += pos
          else vars += name
        }

        block.foreach {
          // Corresponds to var a = ...
          case Defn.Val(_, List(Pat.Var(name)), _, _)             => updateVars(name.value, name.pos)
          case Defn.Var.After_4_7_2(_, List(Pat.Var(name)), _, _) => updateVars(name.value, name.pos)
          // Examine paramclauses e.g. def foo(a: Int) or class bar(b: Int)
          case Term.ParamClause(params, _) => params.foreach(p => updateVars(p.name.value, p.name.pos))

          // Blocks {} and templates
          case Term.Block(stats)                       => collectInner(stats, vars.clone(), flagged)
          case Template.After_4_4_0(_, _, _, stats, _) => collectInner(stats, vars.clone(), flagged)

          case Defn.Def.After_4_6_0(_, _, paramClauseGroup, _, Term.Block(stats)) =>
            // Collect parameters in method e.g. def(a : Int) = ...
            if (paramClauseGroup.isDefined) paramClauseGroup.get.paramClauses.foreach(p => p.values.foreach(param => updateVars(param.name.value, param.name.pos)))
            collectInner(stats, vars.clone(), flagged)

          // Examine for loop body recursively. Condition cannot be a block so no need to be examined
          case Term.For(_, body) => collectInner(normalize(body), vars.clone(), flagged)
          // Examine the body of the if condition and the body of the else condition recursively
          case Term.If.After_4_4_0(_, thenBody, elseBody, _) => collectInner(normalize(thenBody), vars.clone(), flagged); collectInner(normalize(elseBody), vars.clone(), flagged)
          // Examine the body of the match cases recursively
          case Term.Match.After_4_4_5(_, cases, _) => handleCases(cases, vars)
          // Examine body, catches and finally condition of the try block recursively
          case Term.Try(body, catches, finallyp) =>
            collectInner(normalize(body), vars.clone(), flagged)
            handleCases(catches, vars)
            if (finallyp.isDefined) collectInner(normalize(finallyp.get), vars.clone(), flagged)
          // Examine the condition and body of the while loop recursively, condition can have block / assignment inside
          case Term.While(condition, body) => collectInner(normalize(condition), vars.clone(), flagged); collectInner(normalize(body), vars.clone(), flagged)

          // Examine partial functions and functions (e.g. l.collect { ... } or l.foreach(e => ...)
          case Term.PartialFunction(cases)        => cases.foreach(c => collectInner(normalize(c.body), vars.clone(), flagged))
          case Term.Function.After_4_6_0(_, body) => collectInner(normalize(body), vars.clone(), flagged)

          // Examine argclauses e.g. l.foreach(e => ...), e => ... is the argclause
          case Term.ArgClause(args, _) => args.foreach(a => collectInner(normalize(a), vars.clone(), flagged))

          // Try to handle everything else
          case t: Stat =>
            t.children.foreach { // t.children is Tree, not compatible with normalize
              case c: Stat                 => collectInner(normalize(c), vars.clone(), flagged)
              case Term.ArgClause(args, _) => args.foreach(a => collectInner(normalize(a), vars.clone(), flagged)) // For some reason, ArgClause is not considered as a Stat even though it inherits from it
              case _                       => ()
            }

          case _ => ()
        }
      }
      val flagged = mutable.HashSet.empty[Position]
      collectInner(block, vars, flagged)
      flagged.map(p => Patch.lint(diag(p))).toList
    }

    def collectWithConstructor(params: Seq[Term.ParamClause], stats: List[Stat]): List[Patch] = {
      val vars = mutable.HashSet.empty[String]
      params.foreach(c => c.values.foreach(p => vars += p.name.value))
      collect(stats, vars)
    }

    // We first look at the templates (start of a definition) i.e. start of a scope (variables don't go outside)
    // We also look at blocks as context themselves, because a variable could be declared in a block and if it is
    // declared again inside of it, it should be flagged
    doc.tree.collect {
      // We don't handle template directly to be able to collect the variables in the constructor
      case Defn.Class.After_4_6_0(_, _, _, Ctor.Primary.After_4_6_0(_, _, params), Template.After_4_4_0(_, _, _, stats, _)) => collectWithConstructor(params, stats)
      case Defn.Trait.After_4_6_0(_, _, _, Ctor.Primary.After_4_6_0(_, _, params), Template.After_4_4_0(_, _, _, stats, _)) => collectWithConstructor(params, stats)
      case Defn.Enum.After_4_6_0(_, _, _, Ctor.Primary.After_4_6_0(_, _, params), Template.After_4_4_0(_, _, _, stats, _))  => collectWithConstructor(params, stats)
      case Defn.Object(_, _, Template.After_4_4_0(_, _, _, stats, _))                                                       => collect(stats, mutable.HashSet.empty[String])
      // Inspect blocks as contexts themselves
      case Term.Block(stats) => collect(stats, mutable.HashSet.empty[String])
    }.flatten.asPatch
  }
}
