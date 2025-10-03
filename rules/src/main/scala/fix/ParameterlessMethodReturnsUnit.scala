/*
rule = ParameterlessMethodReturnsUnit
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class ParameterlessMethodReturnsUnit extends SemanticRule("ParameterlessMethodReturnsUnit") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for methods returning Unit that are defined without empty parentheses.",
    pos,
    "Methods should be defined with empty parentheses if they have side effects. A method returning Unit must have side effects, therefore you should declare it with ().",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to def myMethod: Unit = ... or def myMethod() = ... with inferred Unit return type
      case t @ Defn.Def.After_4_7_3(_, _, Nil, Some(Type.Name("Unit")), _) => Patch.lint(diag(t.pos))
      case t @ Defn.Def.After_4_7_3(_, _, _, None, _) if Util.matchType(t, "scala/Unit") =>
        Patch.lint(diag(t.pos))
      case t @ Defn.Def.After_4_6_0(_, _, _, None, _) =>
        t.symbol.info.collect(s =>
          s.signature match {
            case ValueSignature(ByNameType(TypeRef(_, tpe, _))) if SymbolMatcher.exact("scala/Unit#").matches(tpe) => Patch.lint(diag(t.pos))
          }
        ).getOrElse(Patch.empty)
    }.asPatch
  }

}
