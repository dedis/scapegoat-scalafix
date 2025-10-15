/*
rule = SuspiciousMatchOnClassObject
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class SuspiciousMatchOnClassObject extends SemanticRule("SuspiciousMatchOnClassObject") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for code where matching is taking place on class literals.",
    pos,
    "Matching on an companion object of a case class is probably not what you intended.",
    LintSeverity.Warning
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    def modsContainsAbstract(mods: List[Mod]): Boolean = mods.exists(m => m.toString() == "abstract")
    val classNames = scala.collection.mutable.Set[String]()
    val objectNames = scala.collection.mutable.Set[String]()
    doc.tree.collect { // Collect all class names defined in the file, excluding abstract classes
      case Defn.Class.After_4_6_0(mods, Type.Name(className), _, _, _) if !modsContainsAbstract(mods) =>
        classNames += className // Non abstract classes
        if (mods.exists(m => m.toString() == "case")) { // Non abstract case classes have companion objects by default
          objectNames += className
        }
      case Defn.Object(mods, Term.Name(objectName), _) if !modsContainsAbstract(mods) => objectNames += objectName
    }
    classNames.filterInPlace(objectNames.contains) // Keep only names that are both class and object names i.e. classes with a companion object

    // We need to iterate twice because the class definitions might be after the match statement

    doc.tree.collect {
      case Term.Match.After_4_4_5(_, cases, _) =>
        cases.collect {
          case c@Case(Term.Name(name), _, _) if classNames.contains(name) => Patch.lint(diag(c.pos))
          case _ => Patch.empty
        }.asPatch
    }.asPatch

  }
}
