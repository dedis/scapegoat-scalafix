/*
rule = VariableShadowing
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._
import scala.collection.mutable
import scala.meta._

class VariableShadowing extends SemanticRule("VariableShadowing") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for multiple uses of the variable name in nested scopes.",
    pos,
    "Variable shadowing is very useful, but can easily lead to nasty bugs in your code. Shadowed variables can be potentially confusing to other maintainers when the same name is adopted to have a new meaning in a nested scope.",
    LintSeverity.Warning
  )
  override def fix(implicit doc: SemanticDocument): Patch = {

    def collect2(tree: Tree, vars: mutable.HashSet[String]) : List[Patch] = {
      val flagged = mutable.HashSet.empty[Position]
      var varsList = vars :: Nil


      def collectInner(tree: Tree, flagged: mutable.HashSet[Position]): mutable.HashSet[Position] = {
        def updateVars(name: String, pos: Position): Unit = {
          if (exists(name)) flagged += pos
          else varsList.headOption.foreach(_ += name)
        }

        def enter() = {
          varsList = mutable.HashSet[String]() :: varsList
        }
        def exit() = varsList = varsList.tail

        def exists(s: String) = varsList.exists(_.contains(s))

        def ece(element: Tree) = {
          enter()
          collectInner(element, flagged)
          exit()
        }

        def ecle(elements: List[Tree]): Unit = {
          enter()
          for (elem <- elements) { collectInner(elem, flagged) }
          exit()
        }

        def paramClauseHandler(paramClauses: List[Member.ParamClauseGroup]): Unit = {
          for (elem <- paramClauses) {
            for (paramClauseGroup <- elem.paramClauses) {
              for (param <- paramClauseGroup.values) {
                updateVars(param.name.value, param.name.pos)
              }
            }
          }
        }

        tree match {
          case t @ (Term.Block(_) | Template.After_4_4_0(_, _, _, _, _) | Term.PartialFunction(_))  => ecle(t.children)
          case Term.Function.After_4_6_0(_, body) => ece(body)
          case Defn.Val(_, List(Pat.Var(name)), _, _)             => updateVars(name.value, name.pos)
          case Defn.Var.After_4_7_2(_, List(Pat.Var(name)), _, _) => updateVars(name.value, name.pos)
          // Examine paramclauses e.g. def foo(a: Int) or class bar(b: Int)
          case Term.ParamClause(params, _) => params.foreach(p => updateVars(p.name.value, p.name.pos))
          case Defn.Def.After_4_7_3(_, _, paramClauseGroups, _ , body) =>
            enter()
            paramClauseHandler(paramClauseGroups)
            collectInner(body, flagged)
            exit()
          case Term.Match.After_4_4_5(_, cases, _) =>
            cases.foreach {
            case Case(Pat.Var(name @ Term.Name(_)), _, body) => enter(); updateVars(name.value, name.pos); collectInner(body, flagged); exit()
            case Case(_, _, body) => ece(body)
            case _ => ()
          }
          case other => ecle(other.children)
        }
        flagged
      }

        collectInner(tree, flagged).map(p => Patch.lint(diag(p))).toList

    }

    def collectWithConstructor(params: Seq[Term.ParamClause], tree: Tree): List[Patch] = {
      val vars = mutable.HashSet.empty[String]
      params.foreach(c => c.values.foreach(p => vars += p.name.value))
      collect2(tree, vars)
    }

    // We first look at the templates (start of a definition) i.e. start of a scope (variables don't go outside)
    // We also look at blocks as context themselves, because a variable could be declared in a block and if it is
    // declared again inside of it, it should be flagged
    doc.tree.collect {
      // We don't handle template directly to be able to collect the variables in the constructor
      case Defn.Class.After_4_6_0(_, _, _, Ctor.Primary.After_4_6_0(_, _, params), t) => collectWithConstructor(params, t)
      case Defn.Trait.After_4_6_0(_, _, _, Ctor.Primary.After_4_6_0(_, _, params), t) => collectWithConstructor(params, t)
      case Defn.Enum.After_4_6_0(_, _, _, Ctor.Primary.After_4_6_0(_, _, params), t)  => collectWithConstructor(params, t)
      case Defn.Object(_, _, t)                                                       => collect2(t, mutable.HashSet.empty[String])
      // Inspect blocks as contexts themselves
      case t @ Term.Block(_) => collect2(t, mutable.HashSet.empty[String])
    }.flatten.asPatch
  }
}
