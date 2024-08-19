/*
rule = UnusedMethodParameter
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

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

    def isNothing(sym: Symbol) = {
      val symbolMatcher = SymbolMatcher.exact("scala/Nothing#")
      sym.info.get.signature match {
        case MethodSignature(_, _, TypeRef(_, tpe, _)) => symbolMatcher.matches(tpe) // Scala 2
        case ValueSignature(TypeRef(_, symbol, _))     => symbolMatcher.matches(symbol) // Scala 3
        case _                                         => false
      }
    }

    def analyzeStats(stats: List[Stat], ctorNotVals: Seq[String], possiblyUnusedFields: List[String], positions: mutable.HashMap[String, Position]): Patch = {
      stats.collect {
        // Ignore main methods and overrides
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
        case Defn.Def.After_4_6_0(_, _, Some(Member.ParamClauseGroup(_, List(Term.ParamClause(params, _)))), _, body) =>
          val paramNames = params.collect {
            // Collect the parameter of the functions which are not marked as unused
            case Term.Param(mods, p @ Term.Name(name), _, _) if !mods.exists(_.toString == "@unused") => positions.put(name, p.pos); name
          }
          val usedParams = body.collect {
            // Collect any parameter that is used in the body
            case p @ Term.Name(name) if paramNames.contains(name) => positions.put(name, p.pos); name
          }

          // Find constructor parameters that are not used in the body
          (ctorNotVals.filterNot(usedParams.contains).map { e =>
            Patch.lint(diag(positions(e)))
          } ++ paramNames.filterNot(usedParams.contains).map { e => // Find method parameters not used in the body
            Patch.lint(diag(positions(e)))
          } ++ possiblyUnusedFields.filterNot(usedParams.contains).map { e => // Find fields that are not used in the body
            Patch.lint(diag(positions(e)))
          }).asPatch
        // The three are combined into a patch

        case _ => Patch.empty
      }.asPatch
    }

    def getPossiblyUnusedFields(stats: List[Stat], positions: mutable.HashMap[String, Position]) = stats.collect {
      case v @ Defn.Var.After_4_7_2(_, List(Pat.Var(name)), _, _) if !isNothing(v.symbol) => positions.put(name.value, v.pos); name.value
      case v @ Defn.Val(_, List(Pat.Var(name)), _, _) if !isNothing(v.symbol)             => positions.put(name.value, v.pos); name.value
    }

    doc.tree.collect {
      case Defn.Trait.After_4_6_0(_, _, _, _, _) => Patch.empty
      case Defn.Object(_, _, Template.After_4_4_0(_, _, _, stats, _)) =>
        val positions = mutable.HashMap[String, Position]()
        val possiblyUnusedFields = getPossiblyUnusedFields(stats, positions)
        analyzeStats(stats, Nil, possiblyUnusedFields, positions) // no constructor for objects
      case Defn.Class.After_4_6_0(mods, _, _, ctor, Template.After_4_4_0(_, _, _, stats, _))
          if !mods.exists(m => m.toString == "abstract") =>
        // Easier to collect positions in a separate HashMap than to collect them in the three later collections and combine them
        val positions = mutable.HashMap[String, Position]()
        val possiblyUnusedFields = getPossiblyUnusedFields(stats, positions)

        /*
         * For constructor params, some params become vals / fields of the class (and should be ignored when unused):
         *   1. all params in the first argument list for case classes
         *   2. all params marked "val", "var" or "unused"
         */

        // In the case where it's a case class, we can ignore all arguments in the first argument list
        // Else we consider everything
        val consideredCtorVals = if (mods.exists(_.toString == "case")) ctor.paramClauses.drop(1) else ctor.paramClauses

        val ctorNotVals = consideredCtorVals
          .flatMap(_.values) // get the values of paramclauses
          .collect { case Term.Param(mods, name, _, _) if !mods.exists(m => m.toString() == "var" || m.toString() == "val" || m.toString() == "@unused") => positions.put(name.value, name.pos); name.value } // get the names of the parameters, when they are not vals / vars or unused

        if (stats.isEmpty && ctorNotVals.nonEmpty) { // If we do not have stats (i.e. no class body), we should check constructor parameters nonetheless
          ctorNotVals.map { e =>
            Patch.lint(diag(positions(e)))
          }.asPatch
        } else analyzeStats(stats, ctorNotVals, possiblyUnusedFields, positions)
    }.asPatch
  }
}
