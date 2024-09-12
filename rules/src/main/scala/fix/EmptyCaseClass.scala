/*
rule = EmptyCaseClass
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class EmptyCaseClass extends SemanticRule("EmptyCaseClass") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for empty case classes like, e.g. case class Faceman().",
    pos,
    "An empty case class can be rewritten as a case object.",
    LintSeverity.Info
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    // For a case class to be empty, it should either have an empty body or no accessors (i.e. just methods, in which case we can have a case object)
    def hasAccessor(body: List[Stat]): Boolean = body.exists {
      case _: Defn.Val => true
      case _: Defn.Var => true
      case _           => false
    }

    def isCaseClass(mods: List[Mod]) = mods.exists { case Mod.Case() => true; case _ => false }

    doc.tree.collect {
      // Corresponds to case class Empty() {} or only with methods, see above
      // We should consider only cases class with empty constructors, no body / accessors and no extending classes
      case c @ Defn.Class.After_4_6_0(mods, _, _, Ctor.Primary.After_4_6_0(_, _, List(Term.ParamClause(Nil, _))), Template.After_4_4_0(_, Nil, _, body, _)) if isCaseClass(mods) && (body.isEmpty || !hasAccessor(body)) => Patch.lint(diag(c.pos))
    }.asPatch
  }
}
