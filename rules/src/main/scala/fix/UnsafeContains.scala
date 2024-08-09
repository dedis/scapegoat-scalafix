/*
rule = UnsafeContains
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class UnsafeContains extends SemanticRule("UnsafeContains") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks `Seq.contains()` and `Option.contains()` for unrelated types.",
    pos,
    "`contains()` method accepts arguments af any type, which means you might be checking if your collection contains an element of an unrelated type.",
    LintSeverity.Error
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    if (doc.synthetics.isEmpty) {
      /* Rule is not fully compatible with Scala 2.13 which doesn't collect synthetics. Since this linter is primarly focused
      on Scala 3, we accept this limitation. There are some edge cases that do not work, see below */
      def isSeqOrOption(term: Term): Boolean = Util.inheritsFrom(term, "scala/collection/Seq#") || Util.inheritsFrom(term, "scala/Option#")

      def rule(qual: Term.Name, arg: Term): Patch = {
        Util.getTypeArgs(qual).headOption match {
          case Some(typeArg) =>
            val argSymbol = arg match {
              case l @ Lit(_)       => Util.litToSymbol(l)
              case t @ Term.Name(_) => Util.getType(t)
              case _                => arg.symbol
            }
            if (Util.inheritsFrom(argSymbol, typeArg.value)) Patch.empty
            else Patch.lint(diag(arg.pos))
          case None => Patch.empty
        }
      }

      doc.tree.collect {
        case Term.Apply.After_4_6_0(Term.Select(qual @ Term.Name(_), Term.Name("contains")), Term.ArgClause(arg :: Nil, _))
            // Note: rule doesn't handle companion object case (e.g. List(1,2,3).contains("olivia")) which should trigger)
            // This comes from the fact that there is currently no way to determine the type of a List built from the
            // companion object with Scalameta semantic document as the return type will simply be list and no information
            // about the type of the elements is stored, except in synthetics. However, Scala 2.13 doesn't collect
            // synthetics and thus this rule is only partial.
            if isSeqOrOption(qual) => rule(qual, arg)
        case Term.ApplyInfix.After_4_6_0(qual @ Term.Name(_), Term.Name("contains"), _, Term.ArgClause(arg :: Nil, _))
            if isSeqOrOption(qual) => rule(qual, arg)
        case _ => Patch.empty
      }.asPatch

    } else {
      val violationSet = scala.collection.mutable.Set[Position]()

      /* This rule works as follows: we look at the synthetics (which contain the information added by the compiler) which
      will notably give us the inferred type. If the type is a UnionType, we know that the contains method is called on an
      element which doesn't have the collection type (because UnionType contains two types).
      However, synthetics don't have the same amount of information as the semantic document, it only appears when
      necessary. For example the code blocks, or class definition will not appear if there is no need for the compiler to
      add information. This means that we cannot directly build a patch from the synthetics as we will miss many empty
      patches. Instead, we will collect the positions of the synthetics that we want to lint, and then we will look at the
      tree to see if the positions are in the tree. If they are, we will lint them.
       */

      doc.synthetics.foreach {
        case TypeApplyTree(OriginalTree(t @ Term.Select(Term.Apply.After_4_6_0(_, _), Term.Name("contains"))), List(UnionType(_))) =>
          violationSet += t.pos
        case TypeApplyTree(OriginalTree(t @ Term.ApplyInfix.After_4_6_0(Term.Name(_), Term.Name("contains"), _, Term.ArgClause(_))), List(UnionType(_))) =>
          violationSet += t.pos
        case TypeApplyTree(OriginalTree(t @ Term.Select(Term.Name(_), Term.Name("contains"))), List(UnionType(_))) =>
          violationSet += t.pos
        case _ => ()
      }

      doc.tree.collect {
        case Term.Apply.After_4_6_0(t @ Term.Select(_ @Term.Name(_), Term.Name("contains")), Term.ArgClause(_ :: Nil, _))
            if violationSet.contains(t.pos) => Patch.lint(diag(t.pos))
        case t @ Term.ApplyInfix.After_4_6_0(Term.Name(_), Term.Name("contains"), _, Term.ArgClause(_ :: Nil, _))
            if violationSet.contains(t.pos) => Patch.lint(diag(t.pos))
        case Term.Apply.After_4_6_0(t @ Term.Select(_, Term.Name("contains")), Term.ArgClause(_ :: Nil, _)) if violationSet.contains(t.pos) => Patch.lint(diag(t.pos))
        case _                                                                                                                              => Patch.empty
      }.asPatch

    }
  }

}
