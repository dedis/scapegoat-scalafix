/*
rule = EmptyIfBlock
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.collection.mutable
import scala.meta._

class EmptyIfBlock extends SemanticRule("EmptyIfBlock") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for empty if blocks.",
    pos,
    "An empty if block is considered as dead code.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    val functionsWithEmptyIfsSet = mutable.HashSet[Position]()

    /* There are some functions where an empty if statement will create a Unit / Any output type which might be desired.
    We thus have to ignore these functions. However, due to how pattern matching works and how we iterate on the tree,
    the if that we have ignored in the function will appear as a separate tree node. We thus have to keep track of the
    ifs that we have ignored in the functions and ignore them when we encounter them in the tree.
     */

    doc.tree.collect {
      // Corresponds to (f => if ( condition ) { }). Note here that we fully ignore the block of the if statement
      // because it doesn't matter if it has a body or not
      case Term.Function.After_4_6_0(_, Term.Block(List(t @ Term.If.After_4_4_0(_, _, _, _)))) => functionsWithEmptyIfsSet.add(t.pos); Patch.empty
      // Corresponds to if ( condition ) { } or if ( condition ) { () }
      case t@Term.If.After_4_4_0(_, Term.Block(Nil | List(Lit.Unit())), _, _) if !functionsWithEmptyIfsSet.contains(t.pos) => Patch.lint(diag(t.pos))

      case _ => Patch.empty
    }.asPatch
  }
}
