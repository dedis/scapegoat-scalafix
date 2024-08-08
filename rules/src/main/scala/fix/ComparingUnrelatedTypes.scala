/*
rule = ComparingUnrelatedTypes
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ComparingUnrelatedTypes extends SemanticRule("ComparingUnrelatedTypes") {

  private def diag(pos: Position) = Diagnostic("", "Checks for equality comparisons that cannot succeed", pos, "In most case comparing unrelated types cannot succeed and it's usually an indication of a bug.", LintSeverity.Error)

  private object LiteralNumeric {
    def unapply(lit: Lit): Option[Lit] = lit match {
      case _ @ (Lit.Byte(_) | Lit.Short(_) | Lit.Char(_) | Lit.Int(_) | Lit.Long(_) | Lit.Float(_) | Lit.Double(_)) => Some(lit)
      case _ => None
    }
  }
  override def fix(implicit doc: SemanticDocument): Patch = {

    doc.tree.collect {
      case Term.ApplyInfix.After_4_6_0(LiteralNumeric(_), Term.Name("==" | "!="), _, Term.ArgClause(List(Lit.Int(0)), _)) => Patch.empty
      case Term.ApplyInfix.After_4_6_0(Lit.Int(0), Term.Name("==" | "!="), _, Term.ArgClause(List(LiteralNumeric(_)), _)) => Patch.empty
      case t @ Term.ApplyInfix.After_4_6_0(value @ Term.Name(_), Term.Name("==" | "!="), _, Term.ArgClause(List(rhs @ Term.Name(_)), _)) =>
        if(Util.inheritsFrom(value, Util.getType(rhs).value) || Util.inheritsFrom(rhs, Util.getType(value).value)) {
          if(Util.getTypeArgs(value).nonEmpty && Util.getTypeArgs(rhs).nonEmpty) {
            if(Util.getTypeArgs(value).head.value == Util.getTypeArgs(rhs).head.value) Patch.empty
            else Patch.lint(diag(t.pos))
          } else Patch.empty
        } else Patch.lint(diag(t.pos))
      case t @ Term.ApplyInfix.After_4_6_0(value , Term.Name("==" | "!="), _, Term.ArgClause(List(rhs), _)) =>

        val sym = value.symbol
        val b1 = Util.inheritsFrom(value, rhs)
        val b2 = Util.inheritsFrom(rhs, value)
        print(sym, rhs.symbol, b1, b2)
        if(!(Util.inheritsFrom(value, rhs) || Util.inheritsFrom(rhs, value))) Patch.lint(diag(t.pos))
        else Patch.empty
    }.asPatch


  }

}
