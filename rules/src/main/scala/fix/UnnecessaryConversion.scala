/*
rule = UnnecessaryConversion
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class UnnecessaryConversion extends SemanticRule("UnnecessaryConversion") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for unnecessary toInt on instances of Int or toString on Strings, etc",
    pos,
    "Calling e.g. toString on a String or toList on a List is completely unnecessary and it's an equivalent to identity.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(Lit.String(_), Term.Name("toString"))                                                   => Patch.lint(diag(t.pos))
      case t @ Term.Select(name @ Term.Name(_), Term.Name("toString")) if Util.matchType(name, "java/lang/String") => Patch.lint(diag(t.pos))

      case t @ Term.Select(Lit.Int(_), Term.Name("toInt"))                                               => Patch.lint(diag(t.pos))
      case t @ Term.Select(name @ Term.Name(_), Term.Name("toInt")) if Util.matchType(name, "scala/Int") => Patch.lint(diag(t.pos))

      case t @ Term.Select(Lit.Long(_), Term.Name("toLong"))                                               => Patch.lint(diag(t.pos))
      case t @ Term.Select(name @ Term.Name(_), Term.Name("toLong")) if Util.matchType(name, "scala/Long") => Patch.lint(diag(t.pos))

      case t @ Term.Select(Lit.Char(_), Term.Name("toChar"))                                               => Patch.lint(diag(t.pos))
      case t @ Term.Select(name @ Term.Name(_), Term.Name("toChar")) if Util.matchType(name, "scala/Char") => Patch.lint(diag(t.pos))

      case t @ Term.Select(Lit.Byte(_), Term.Name("toByte"))                                               => Patch.lint(diag(t.pos))
      case t @ Term.Select(name @ Term.Name(_), Term.Name("toByte")) if Util.matchType(name, "scala/Byte") => Patch.lint(diag(t.pos))

      case t @ Term.Select(Lit.Short(_), Term.Name("toShort"))                                               => Patch.lint(diag(t.pos))
      case t @ Term.Select(name @ Term.Name(_), Term.Name("toShort")) if Util.matchType(name, "scala/Short") => Patch.lint(diag(t.pos))

      case t @ Term.Select(Lit.Float(_), Term.Name("toFloat"))                                               => Patch.lint(diag(t.pos))
      case t @ Term.Select(name @ Term.Name(_), Term.Name("toFloat")) if Util.matchType(name, "scala/Float") => Patch.lint(diag(t.pos))

      case t @ Term.Select(Lit.Double(_), Term.Name("toDouble"))                                               => Patch.lint(diag(t.pos))
      case t @ Term.Select(name @ Term.Name(_), Term.Name("toDouble")) if Util.matchType(name, "scala/Double") => Patch.lint(diag(t.pos))

      // Predef.Set corresponds to Set(_) in Scala, same for Map
      case t @ Term.Select(qual, Term.Name("toSet")) if Util.matchType(qual, "scala/collection/immutable/Set", "scala/collection/mutable/Set", "scala/Predef.Set") => Patch.lint(diag(t.pos))
      case t @ Term.Select(qual, Term.Name("toMap")) if Util.matchType(qual, "scala/collection/immutable/Map", "scala/collection/mutable/Map", "scala/Predef.Map") => Patch.lint(diag(t.pos))
      // package.List corresponds to List(_) in Scala, same for Seq and Vector
      case t @ Term.Select(qual, Term.Name("toList")) if Util.matchType(qual, "scala/collection/immutable/List", "scala/package.List")                              => Patch.lint(diag(t.pos))
      case t @ Term.Select(qual, Term.Name("toSeq")) if Util.matchType(qual, "scala/collection/immutable/Seq", "scala/collection/mutable/Seq", "scala/package.Seq") => Patch.lint(diag(t.pos))
      case t @ Term.Select(qual, Term.Name("toVector")) if Util.matchType(qual, "scala/collection/immutable/Vector", "scala/package.Vector")                        => Patch.lint(diag(t.pos))
      case t @ Term.Select(qual, Term.Name("toArray")) if Util.matchType(qual, "scala/Array", "scala/Array.empty")                                                  => Patch.lint(diag(t.pos))
      case _                                                                                                                                                        => Patch.empty
    }.asPatch
  }
}
