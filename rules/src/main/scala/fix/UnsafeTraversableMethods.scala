/*
rule = UnsafeTraversableMethods
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._
import scalafix.util._
import scala.meta._

class UnsafeTraversableMethods extends SemanticRule("UnsafeTraversableMethods") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Use of unsafe traversable methods",
    pos,
    "The following methods on Traversable are considered to be unsafe (head, tail, init, last, reduce, reduceLeft, reduceRight, max, maxBy, min, minBy).",
    LintSeverity.Error
  )

  private val unsafeMethods = Set(
    "head",
    "tail",
    "init",
    "last",
    "reduce",
    "reduceLeft",
    "reduceRight",
    "max",
    "maxBy",
    "min",
    "minBy"
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(qual @ Term.Name(_), Term.Name(str))
          if unsafeMethods.contains(str)
            && Util.inheritsFrom(qual, "scala/collection/Iterable#") => Patch.lint(diag(t.pos))
      case t @ Term.Select(Term.Apply.After_4_6_0(qual @ Term.Name(_), _), Term.Name(str))
          if unsafeMethods.contains(str)
            && Util.inheritsFrom(qual, "scala/collection/Iterable#") => Patch.lint(diag(t.pos))
      case _ => Patch.empty
    }.asPatch
  }

}
