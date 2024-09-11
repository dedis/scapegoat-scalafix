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
    "Checks for vars that could be declared as vals.",
    pos,
    "A variable (var) that is never written to could be turned into a value (val).",
    LintSeverity.Warning
  )
  override def fix(implicit doc: SemanticDocument): Patch = {

    // See: https://scala-lang.org/files/archive/spec/2.13/06-expressions.html#assignment-operators
    val notAssignmentsSet = Set("<=", ">=", "!=")
    def isAssignment(op: String) = op.endsWith("=") && !notAssignmentsSet.contains(op)

    def collect(tree: Tree): Patch = {
      val vars: mutable.HashMap[String, Position] = mutable.HashMap()
      tree.traverse {
        case Defn.Var.After_4_7_2(_, List(Pat.Var(name)), _, _) => vars.put(name.value, name.pos)
        // Corresponds to a = ...
        case Term.Assign(Term.Name(name), _) => vars.remove(name)
        // Corresponds to compound assignments e.g. +=, -=, etc.
        case Term.ApplyInfix.After_4_6_0(Term.Name(name), Term.Name(op), _, _) if isAssignment(op) => vars.remove(name)
      }
        vars.map(name => Patch.lint(diag(name._2))).asPatch
    }

    // We first look at the templates (start of a definition) i.e. start of a scope (variables don't go outside)
    // We also look at blocks as context themselves, because a variable could be declared in a block and if it is
    // not reused inside of it, it should be flagged
    doc.tree.collect {
      // Corresponds to class, trait, object...
      case t: Template => collect(t)
      // Inspect blocks as contexts themselves
      case t: Term.Block => collect(t)
    }.distinct.asPatch
  }
}
