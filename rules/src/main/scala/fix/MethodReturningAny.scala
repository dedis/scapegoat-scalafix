/*
rule = MethodReturningAny
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class MethodReturningAny extends SemanticRule("MethodReturningAny") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for functions that are defined to return Any.",
    pos,
    "Method returns Any. Consider using a more specialized type.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Note: this rule is limited since there is no type inference done, we cannot use synthetics either since they
      // do not seem to provide information about definitions and return types. This rule is thus limited to the type put by the developer.
      case t @ Defn.Def.After_4_6_0(mods, _, _, Some(Type.Name("Any" | "AnyRef")), _) if !mods.exists(m => m.toString() == "override") => Patch.lint(diag(t.pos))
      case t @ Defn.Def.After_4_6_0(mods, _, _, None, _) if !mods.exists(m => m.toString() == "override") =>
        t.symbol.info.collect(s =>
          s.signature match {
            // Type was inferred to be Any or AnyRef
            case MethodSignature(_, _, TypeRef(_, tpe, _)) if SymbolMatcher.exact("scala/Any#", "scala/AnyRef#").matches(tpe) => Patch.lint(diag(t.pos))
            case MethodSignature(_, _, UnionType(_))                                                                          => Patch.lint(diag(t.pos))
            // Flag soft (i.e. compiler inferred) UnionTypes, as chances are the user would not want that.
            // If a UnionType is actually desired, one can simply add the type explicitly. The ParamClauseGroup would not be None in that case.
            case _ => Patch.empty
          }
        ).getOrElse(Patch.empty)
    }.asPatch
  }
}
