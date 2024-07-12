/*
rule = UnsafeContains
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class UnsafeContains extends SemanticRule("UnsafeContains") {

  private def diag(pos: Position) = Diagnostic("", "Checks `Seq.contains()` and `Option.contains()` for unrelated types.", pos, "`contains()` method accepts arguments af any type, which means you might be checking if your collection contains an element of an unrelated type.", LintSeverity.Error)

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isSeqOrOption(term: Term): Boolean = Util.inheritsFrom(term, "scala/collection/Seq#") || Util.inheritsFrom(term, "scala/Option#")

    def rule(qual: Term.Name, arg: Term): Patch ={
      Util.getTypeArgs(qual).headOption match {
        case Some(typeArg) =>
          val argSymbol = arg match {
            case l @ Lit(_) => Util.litToSymbol(l)
            case t @ Term.Name(_) => Util.getType(t)
            case _ => arg.symbol
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
        // companion object with Scalameta as the return type will simply be list and no information about the type of
        // the elements is stored. We could come up with something that determines that infers the type of the elements
        // but that would be a lot of work for a small edge case. Lists and other structures inheriting from Seq can
        // indeed store multiple types and we cannot simply look at the type of the first element.
        if isSeqOrOption(qual) => rule(qual, arg)
      case Term.ApplyInfix.After_4_6_0(qual @ Term.Name(_), Term.Name("contains"), _, Term.ArgClause(arg :: Nil, _))
        if isSeqOrOption(qual) => rule(qual, arg)
      case _ => Patch.empty
    }.asPatch

  }

}
