/*
rule = DuplicateMapKey
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.collection.mutable
import scala.meta._

class DuplicateMapKey extends SemanticRule("DuplicateMapKey") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for duplicate key names in Map literals.",
    pos,
    "A map key is overwritten by a later entry.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(Term.Name("Map"), Term.ArgClause(args, _)) =>
        val seen = mutable.HashSet.empty[String]
        args.collect {
          // Simply look at the args, try to add them in the sent, will fail if already present.
          // We chose not to support the unicode arrow (→) because it has been deprecated in Scala 2.13.
          case Term.ApplyInfix.After_4_6_0(Lit.String(key), Term.Name("->"), _, _) if !seen.add(key) => Patch.lint(diag(t.pos))
        }
    }.flatten.asPatch
  }

}
