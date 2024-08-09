/*
rule = CollectionNegativeIndex
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class CollectionNegativeIndex extends SemanticRule("CollectionNegativeIndex") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for negative access on a sequence, e.g. list.get(-1).",
    pos,
    "Trying to access Seq elements using a negative index will result in an IndexOutOfBoundsException.",
    LintSeverity.Error
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isSeqOrArray(qual: Term): Boolean = {
      // Array does not inherit from Seq but since Array(-1) will throw an exception, we need to consider it in the rule
      Util.matchType(qual, "scala/Array#") || Util.inheritsFrom(qual, "scala/collection/Seq#")
    }

    doc.tree.collect {
      // Note: this rule is more general than the Scapegoat one which only targeted lists, here we target all Seqs
      case t @ Term.Apply.After_4_6_0(qual @ Term.Name(_), Term.ArgClause(Lit.Int(value) :: Nil, _)) if value < 0 && isSeqOrArray(qual)                            => Patch.lint(diag(t.pos))
      case t @ Term.Apply.After_4_6_0(Term.Apply.After_4_6_0(qual @ Term.Name(_), _), Term.ArgClause(Lit.Int(value) :: Nil, _)) if value < 0 && isSeqOrArray(qual) => Patch.lint(diag(t.pos))
    }.asPatch

  }

}
