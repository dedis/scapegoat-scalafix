/*
rule = DuplicateImport
 */
package fix

import scalafix.lint.LintSeverity
import scalafix.v1._

import scala.meta._

class DuplicateImport extends SemanticRule("DuplicateImport") {

  private def diag(pos: Position) = Diagnostic(
    "",
    "Checks for duplicate import statements.",
    pos,
    "Duplicate imports should be removed.",
    LintSeverity.Info
  )
  override def fix(implicit doc: SemanticDocument): Patch = {
    val importSet = scala.collection.mutable.HashSet[String]()
    var patchList = List[Patch]()
    doc.tree.traverse {
      case Pkg(_, _) => importSet.clear() // We do this to avoid flagging cases where we import in two different scopes
      // but in the same document twice, which means that it's not actually a duplicate import
      case Defn.Class.After_4_6_0(_, _, _, _, _) => importSet.clear()
      case Defn.Trait.After_4_6_0(_, _, _, _, _) => importSet.clear()
      case Defn.Object(_, _, _)                  => importSet.clear()
      case Import(importers)                     =>
        // We use a patch list and traverse instead of collecting as the above cases do not lead to a Patch and would
        // require us to create lists of empty patches for each one, i.e. it would become
        // "importSet.clear(); List(Patch.empty) which is not ideal. We instead choose to collect the patches through
        // traversal and store them in a list which gives us more flexibility
        patchList ++= importers.collect {
          // The importer is the name of the parent object of the imported object e.g. scala.collection.immutable
          // and the is the actual imported object, the selector, e.g. Set or Seq or {Set, Seq} (hence the nested collect call)
          case Importer(ref, importees) => importees.collect { importee =>
              val name = ref.toString() + "." + importee.toString()
              if (importSet.contains(name)) Patch.lint(diag(importee.pos))
              else {
                importSet.add(name)
                Patch.empty
              }
            }
        }.flatten
    }
    patchList.asPatch
  }
}
