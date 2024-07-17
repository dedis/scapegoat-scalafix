/*
rule = UnsafeStringContains
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class UnsafeStringContains extends SemanticRule("UnsafeStringContains") {

  private def diag(pos: Position) = Diagnostic("", "Checks for String.contains(value) for invalid types.", pos, "String.contains() accepts arguments af any type, which means you might be checking if your string contains an element of an unrelated type.", LintSeverity.Error)

  override def fix(implicit doc: SemanticDocument): Patch = {

    def isCompatibleType(arg: Term): Boolean = arg match {
      case Lit.String(_) | Lit.Char(_) => true // Corresponds to String.contains("a") or String.contains('a')
      case Term.Ascribe(_, Type.Name("Char")) => true // Corresponds to String.contains(2: Char)
      case Term.ApplyType.After_4_6_0(Term.Select(_, Term.Name("asInstanceOf")), Type.ArgClause(Type.Name("Char") :: Nil)) => true // Corresponds to String.contains(2.asInstanceOf[Char])
      case _ => Util.inheritsFrom(arg, "java/lang/CharSequence#") || Util.inheritsFrom(arg, "scala/Char#")
    }

    doc.tree.collect {
      case Term.Apply.After_4_6_0(Term.Select(qual, Term.Name("contains")), Term.ArgClause(arg :: Nil, _))
        if Util.matchType(qual, "scala/Predef.String") && !isCompatibleType(arg) => Patch.lint(diag(arg.pos))
      case Term.Apply.After_4_6_0(Term.Select(Lit.String(_), Term.Name("contains")), Term.ArgClause(arg :: Nil, _))
        if !isCompatibleType(arg) => Patch.lint(diag(arg.pos))
      case _ => Patch.empty
    }.asPatch

  }

}
