/*
rule = VarCouldBeVal
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.annotation.nowarn
import scala.collection.mutable
import scala.meta._

class VarCouldBeVal extends SemanticRule("VarCouldBeVal") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for vars that could be declared as vals..",
    pos,
    "A variable (var) that is never written to could be turned into a value (val).",
    LintSeverity.Warning
  )
  override def fix(implicit doc: SemanticDocument): Patch = {

    // See: https://scala-lang.org/files/archive/spec/2.13/06-expressions.html#assignment-operators
    val notAssignmentsSet = Set("<=", ">=", "!=")
    def isAssignment(op: String) = op.endsWith("=") && !notAssignmentsSet.contains(op)

    def collect(block: List[Stat]): List[Patch] = {
      // Extracts the stats from blocks and otherwise converts them so that we can do a foreach on them
      def normalize(term: Stat): List[Stat] = term match {
        case Term.Block(stats) => stats
        case _                 => List(term)
      }

      @nowarn // this is because in Scala 2, t.children at line 47 is a List[Tree], but in Scala 3 it is a List[Stat], so we need to suppress the warning for the unreachable case case _ in Scala 3
      def collectInner(block: List[Stat], vars: mutable.HashMap[String, Position]): Unit = {
        block.foreach {
          // Corresponds to var a = ...
          case Defn.Var.After_4_7_2(_, List(Pat.Var(name)), _, _) => vars.put(name.value, name.pos)
          // Corresponds to a = ...
          case Term.Assign(Term.Name(name), _) if vars.exists(v => v._1 == name) => vars.remove(name)
          // Corresponds to compound assignments e.g. +=, -=, etc.
          case Term.ApplyInfix.After_4_6_0(Term.Name(name), Term.Name(op), _, _) if isAssignment(op) => vars.remove(name)

          // Blocks {} and templates
          case Term.Block(stats) => collectInner(stats, vars)

          // Examine for loop body recursively. Condition cannot be a block so no need to be examined
          case Term.For(_, body) => collectInner(normalize(body), vars)
          // Examine the body of the if condition and the body of the else condition recursively
          case Term.If.After_4_4_0(_, thenBody, elseBody, _) => collectInner(normalize(thenBody), vars); collectInner(normalize(elseBody), vars)
          // Examine the body of the match cases recursively
          case Term.Match.After_4_4_5(_, cases, _) => cases.foreach(c => collectInner(normalize(c.body), vars))
          // Examine body, catches and finally condition of the try block recursively
          case Term.Try(body, catches, finallyp) =>
            collectInner(normalize(body), vars)
            catches.foreach(c => collectInner(normalize(c.body), vars))
            if (finallyp.isDefined) collectInner(normalize(finallyp.get), vars)
          // Examine the condition and body of the while loop recursively, condition can have block / assignment inside
          case Term.While(condition, body) => collectInner(normalize(condition), vars); collectInner(normalize(body), vars)

          // Examine partial functions and functions (e.g. l.collect { ... } or l.foreach(e => ...)
          case Term.PartialFunction(cases)        => cases.foreach(c => collectInner(normalize(c.body), vars))
          case Term.Function.After_4_6_0(_, body) => collectInner(normalize(body), vars)

          // Examine argclauses e.g. l.foreach(e => ...), e => ... is the argclause
          case Term.ArgClause(args, _) => args.foreach(a => collectInner(normalize(a), vars))

          // Try to handle everything else
          case t: Stat =>
            t.children.foreach { // t.children is Tree, not compatible with normalize
              case c: Stat                 => collectInner(normalize(c), vars)
              case Term.ArgClause(args, _) => args.foreach(a => collectInner(normalize(a), vars)) // For some reason, ArgClause is not considered as a Stat even though it inherits from it
              case _                       => ()
            }

          case _ => ()
        }
      }

      val vars = mutable.HashMap.empty[String, Position]
      collectInner(block, vars)
      vars.map(name => Patch.lint(diag(name._2))).toList
    }

    // We first look at the templates (start of a definition) i.e. start of a scope (variables don't go outside)
    // We also look at blocks as context themselves, because a variable could be declared in a block and if it is
    // not reused inside of it, it should be flagged
    doc.tree.collect {
      // Corresponds to class, trait, object...
      case Template.After_4_4_0(_, _, _, stats, _) => collect(stats)
      // Inspect blocks as contexts themselves
      case Term.Block(stats) => collect(stats)
    }.flatten.asPatch
  }
}
