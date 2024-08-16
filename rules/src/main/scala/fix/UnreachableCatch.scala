/*
rule = UnreachableCatch
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.collection.mutable
import scala.meta._

class UnreachableCatch extends SemanticRule("UnreachableCatch") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for catch clauses that cannot be reached.",
    pos,
    "One or more cases are unreachable.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {

    doc.tree.collect {
      case Term.Try(_, cases, _) =>
        val types = mutable.HashSet[String]()
        cases.collect {
          case c @ Case(Pat.Typed(_, tpe), guard, _) =>
            if (types.exists(t => Util.inheritsFrom(tpe.symbol, t))) Patch.lint(diag(c.pos))
            else {
              tpe.symbol.info.foreach(_.signature match {
                case TypeSignature(_, _, TypeRef(_, symbol, _)) if guard.isEmpty => types.add(symbol.value) // Extract upperbound symbol of type
                // We only add it if there is no guard because if we have twice the same type they trivially inherit from each other.
                // By adding only if there is no guard, we will flag later ones with guards as unreachable, because they will be. See test case 2
                case _ => ()
              })
              Patch.empty
            }
          case _ => Patch.empty
        }
    }.flatten.asPatch
  }
}
