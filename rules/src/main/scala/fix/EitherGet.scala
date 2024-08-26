/*
rule = EitherGet
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class EitherGet extends SemanticRule("EitherGet") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for use of .get on Left or Right projection.",
    pos,
    "Method .get on a Left and a Right projection is deprecated since 2.13, use Either.getOrElse or Either.swap.getOrElse instead.",
    LintSeverity.Error
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // Corresponds to Left(_).get or Right(_).get, scala/package.Left or scala/package.Right is for companion object
      case t @ Term.Select(Term.Select(qual, Term.Name("left" | "right")), Term.Name("get")) if Util.matchType(qual, "scala/util/Left", "scala/util/Right", "scala/package.Left", "scala/package.Right") => Patch.lint(diag(t.pos))
    }.asPatch
  }
}
