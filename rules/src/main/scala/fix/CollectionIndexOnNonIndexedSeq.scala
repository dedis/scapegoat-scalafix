/*
rule = CollectionIndexOnNonIndexedSeq
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class CollectionIndexOnNonIndexedSeq extends SemanticRule("CollectionIndexOnNonIndexedSeq") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for indexing on a Seq which is not an IndexedSeq.",
    pos,
    "Using an index to access elements of an IndexedSeq may cause performance problems",
    LintSeverity.Error
  )
  override def fix(implicit doc: SemanticDocument): Patch = {

    def isNonIndexedSeq(qual: Term): Boolean = {
      Util.inheritsFrom(qual, "scala/collection/Seq#") && !Util.inheritsFrom(qual, "scala/collection/IndexedSeq#")
    }

    def isLiteral(arg: Term): Boolean = {
      arg match {
        case Lit(_) => true
        case _      => false
      }
    }

    doc.tree.collect {
      // Note: we put a Term.ArgClause(_ :: Nil) to ensure that the method has exactly one argument, not no arguments (e.g. to ignore List())
      case t @ Term.Apply.After_4_6_0(qual @ Term.Name(_), Term.ArgClause(arg :: Nil, _)) if isNonIndexedSeq(qual) && !isLiteral(arg)                            => Patch.lint(diag(t.pos))
      case t @ Term.Apply.After_4_6_0(Term.Apply.After_4_6_0(qual @ Term.Name(_), _), Term.ArgClause(arg :: Nil, _)) if isNonIndexedSeq(qual) && !isLiteral(arg) => Patch.lint(diag(t.pos))
    }.asPatch

  }

}
