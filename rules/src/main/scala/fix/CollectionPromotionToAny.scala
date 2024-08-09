/*
rule = CollectionPromotionToAny
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class CollectionPromotionToAny extends SemanticRule("CollectionPromotionToAny") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for collection operations that promote the collection to Any.",
    pos,
    "The `:+` (append) operator on collections accepts any argument you give it, which means that you can end up with e.g. `Seq[Any]` if your types don't match.",
    LintSeverity.Error
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    /* This rule works as follows: we first get the type arguments of the collection and check if there is only one.
     If there are multiple (e.g. UnionType(Int,String)), this becomes Any so we ignore it. We then check if that single
     type is Any; if so, we can safely skip as there is no risk of promotion. If the type is not Any, we check if the
     argument of the `:+` operation is of the same type as the type arg of the collection. If it is not, we trigger a lint.
     */
    doc.tree.collect {
      case Term.ApplyInfix.After_4_6_0(lhs, Term.Name(":+"), _, Term.ArgClause(List(rhs), _)) =>
        Util.getTypeArgs(lhs) match {
          case List(tpe) if tpe.value != "scala/Any#" =>
            val convertedRhs = rhs match { // Magic to convert rhs to a symbol
              case l: Lit       => Util.litToSymbol(l)
              case Term.Name(_) => Util.getType(rhs)
              case _            => rhs.symbol
            }
            if (SymbolMatcher.normalized(tpe.value).matches(convertedRhs)) Patch.empty
            else Patch.lint(diag(rhs.pos))
          case _ => Patch.empty
        }
    }.asPatch

  }

}
