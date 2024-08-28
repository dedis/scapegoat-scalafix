/*
rule = UnusedMethodParameter
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.annotation.nowarn
import scala.collection.mutable
import scala.meta._

class UnusedMethodParameter extends SemanticRule("UnusedMethodParameter") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for unused method parameters.",
    pos,
    "Unused constructor or method parameters should be removed.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    def normalize(term: Stat): List[Stat] = term match {
      case Term.Block(stats) => stats
      case _                 => List(term)
    }
    def getFields(stats: List[Stat]): Map[String, Position] = stats.collect {
      case Defn.Var.After_4_7_2(_, List(Pat.Var(name)), _, _) => (name.value, name.pos)
      case Defn.Val(_, List(Pat.Var(name)), _, _)             => (name.value, name.pos)
    }.toMap

    @nowarn
    def unusedParams(stats: List[Stat], params: Set[String]): Set[String] = {

      def processArgClause(args: List[Term], acc: Set[String]): Set[String] = args.foldLeft(acc) {
        case (acc, e: Stat) => unusedParams(normalize(e), acc)
        case (acc, _)       => acc
      }

      if (stats.isEmpty) params
      else stats.foldLeft(params) {
        case (acc, Term.Name(name)) if acc.contains(name) => acc - name
        case (acc, others: Stat) => others.children.foldLeft(acc) {
            case (acc, e: Stat) => unusedParams(normalize(e), acc)
            // Argclause is not a stat, we recurse on arguments
            case (acc, Term.ArgClause(args, _)) => processArgClause(args, acc)
            case (acc, _)                       => acc
          }
        case (acc, _) => acc
      }
    }

    doc.tree.collect {
      case Defn.Trait.After_4_6_0(_, _, _, _, _) => Patch.empty
      case Defn.Object(_, _, Template.After_4_4_0(_, _, _, stats, _)) =>
        val fields = getFields(stats)
        unusedParams(stats, fields.keySet).map(e =>
          Patch.lint(diag(fields(e)))
        ).asPatch
      case Defn.Class.After_4_6_0(mods, _, _, ctor, Template.After_4_4_0(_, _, _, stats, _))
          if !mods.exists(m => m.toString == "abstract") =>
        /*
         * For constructor params, some params become vals / fields of the class (and should be ignored when unused):
         *   1. all params in the first argument list for case classes
         *   2. all params marked "val", "var" or "unused"
         */

        // In the case where it's a case class, we can ignore all arguments in the first argument list
        // Else we consider everything
        val consideredCtorVals = if (mods.exists(_.toString == "case")) ctor.paramClauses.drop(1) else ctor.paramClauses

        val ctorNotVals: Map[String, Position] = consideredCtorVals
          .flatMap(_.values) // get the values of paramclauses
          .collect {
            case t @ Term.Param(mods, name, _, _) if !mods.exists(m => m.toString() == "var" || m.toString() == "val" || m.toString() == "@unused") => (name.value, t.pos)
          }.toMap
        val fields = getFields(stats)
        unusedParams(stats, fields.keySet ++ ctorNotVals.keySet).map(e =>
          Patch.lint(diag(fields.getOrElse(e, ctorNotVals(e))))
        ).asPatch

      case Defn.Def.After_4_6_0(_, Term.Name("main"), Some(Member.ParamClauseGroup(_, List(Term.ParamClause(List(Term.Param(_, Term.Name("args"), Some(Type.Apply.After_4_6_0(Type.Name("Array"), Type.ArgClause(List(Type.Name("String"))))), _)), _)))), _, _) => Patch.empty
      // Looking at mods directly e.g. @main def hello = () or override def hello = ()
      case Defn.Def.After_4_6_0(mods, _, _, _, _) if mods.exists(m => m.toString == "@main" || m.toString == "override") => Patch.empty
      // Some overridden methods are not marked as overridden but this is stored in symbol information
      case d @ Defn.Def.After_4_6_0(_, _, _, _, _) if d.symbol.info.exists(_.overriddenSymbols.nonEmpty) => Patch.empty

      // Ignore nothing methods
      case Defn.Def.After_4_6_0(_, _, _, Some(Type.Name("Nothing")), _) => Patch.empty // Methods declared returning Nothing
      case t @ Defn.Def.After_4_6_0(_, _, _, _, _) if t.symbol.info.map(_.signature).exists {
            case MethodSignature(_, _, TypeRef(_, tpe, _)) => SymbolMatcher.exact("scala/Nothing#").matches(tpe) // Methods inferred to return Nothing
            case _                                         => false
          } => Patch.empty

      // Any other method
      case Defn.Def.After_4_6_0(_, _, paramClause, _, body) =>
        // Method might not have params
        val params = paramClause match {
          case Some(Member.ParamClauseGroup(_, List(Term.ParamClause(paramsValue, _)))) => paramsValue
          case _                                                                        => Nil
        }
        val paramNames = params.collect {
          // Collect the parameter of the functions which are not marked as unused
          case Term.Param(mods, p @ Term.Name(name), _, _) if !mods.exists(_.toString == "@unused") => (name, p.pos)
        }.toMap

        unusedParams(normalize(body), paramNames.keySet).map(e =>
          Patch.lint(diag(paramNames(e)))
        ).asPatch
    }
  }.asPatch
}
