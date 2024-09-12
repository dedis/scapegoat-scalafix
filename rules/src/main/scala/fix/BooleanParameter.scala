/*
rule = BooleanParameter
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class BooleanParameter extends SemanticRule("BooleanParameter") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for functions that have a Boolean parameter.",
    pos,
    "Method has Boolean parameter. Consider splitting into two methods or using a case class.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def shouldBeIgnored(mods: List[Mod]) = mods.exists {
      case Mod.Override()                                                    => true
      case Mod.Annot(Init.After_4_6_0(Type.Name("getter" | "setter"), _, _)) => true
      case Mod.Abstract()                                                    => true
      case _                                                                 => false
    }

    def hasBooleanParameter(paramClauseGroups: List[Member.ParamClauseGroup]) = paramClauseGroups.exists {
      case Member.ParamClauseGroup(_, paramClauses) => paramClauses.exists {
          case Term.ParamClause(params, _) => params.exists {
              case Term.Param(_, _, Some(Type.Name("Boolean")), _) => true
              case _                                               => false
            }
          case _ => false
        }
      case _ => false
    }

    doc.tree.collect {
      // Corresponds to def foo(..., bool: Boolean, ...) where ... is a list of parameters (possibly empty). Also handles function with multiple param clauses
      case d @ Defn.Def.After_4_7_3(mods, _, paramClauseGroups, _, _) if !shouldBeIgnored(mods) && hasBooleanParameter(paramClauseGroups) => Patch.lint(diag(d.pos))
    }.asPatch
  }

}
