/*
rule = DuplicateSetValue
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.collection.mutable
import scala.meta._

class DuplicateSetValue extends SemanticRule("DuplicateSetValue") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for duplicate values in set literals.",
    pos,
    "A set value is overwritten by a later entry.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(Term.Name("Set"), Term.ArgClause(args, _)) =>
        val seen = mutable.HashSet.empty[Any]
        args.collect {
          // Simply look at the args, try to add them in the sent, will fail if already present.
          // We limit ourselves to literals, as variables could change at runtime, see tests.
          // We work with the value, not the literal, as otherwise comparisons do not function.
          case value: Lit if !seen.add(value.value) => Patch.lint(diag(t.pos))
        }
    }.flatten.asPatch
  }

}
