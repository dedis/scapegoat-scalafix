package fix

import scalafix.v1._

import scala.annotation.tailrec
import scala.meta._

/** Utilities class for linter */
object Util {

  /** Get the type of a val/var.
    * @param term
    *   The term / stat to get the type of
    * @param doc
    *   The document to get the semantic information from
    * @return
    *   The type of the val/var, or Symbol.None if not found
    */
  // Type information is stored in the ValueSignature of the Term if it is a val/var.
  // We pass the term a Stat as they have the same information, we can thus handle more cases and Term is a child of Stat
  def getType(term: Stat)(implicit doc: SemanticDocument): Symbol = {
    term.symbol.info match {
      case Some(symInfo) => symInfo.signature match {
          case ValueSignature(TypeRef(_, symbol, _)) => symbol
          case _                                     => Symbol.None
        }
      case _ => Symbol.None
    }
  }

  /** Compare the type of a term with the passed symbols
    * @param term
    *   The term to compare
    * @param symbols
    *   The symbols to compare with
    * @param doc
    *   The document to get the semantic information from
    * @return
    *   Whether or not the term matches one of the symbols
    */
  def matchType(term: Stat, symbols: String*)(implicit doc: SemanticDocument): Boolean = {
    val symbolMatcher = SymbolMatcher.normalized(symbols: _*)
    symbolMatcher.matches(term.symbol) || symbolMatcher.matches(getType(term))
    // Checks the term symbol matches that of the symbol (i.e. when we use the type directly),
    // or if the type of the variable matches.
  }

  /** Find definition of val / var.
    * @param tree
    *   The tree to search in
    * @param name
    *   The name of the val / var to search for
    * @return
    *   The definition of the val / var if found, null otherwise
    */
  // We simply explore the tree and check the definitions and return if a name matches
  def findDefinition(tree: Tree, name: Term): Any = {
    tree.collect {
      case Defn.Val(_, List(Pat.Var(varName)), _, value)
          if varName.value == name.toString => value
      case Defn.Var.After_4_7_2(_, List(Pat.Var(varName)), _, value)
          if varName == name => value
    }.headOption.orNull
  }

  /** Find multiple definitions in one tree traversal.
    * @param tree
    *   The tree to search in
    * @param nameSet
    *   The set of names to search for
    * @return
    *   An unordered list of definitions found
    */
  // Takes a set as argument as we will have to do many lookups
  def findDefinitions(tree: Tree, nameSet: Set[Term]): List[(Term, Any)] = {
    tree.collect {
      case Defn.Val(_, List(Pat.Var(varName)), _, value) if nameSet.exists(_.toString().equals(varName.value))             => nameSet.find(_.toString().equals(varName.value)).get -> value
      case Defn.Var.After_4_7_2(_, List(Pat.Var(varName)), _, value) if nameSet.exists(_.toString().equals(varName.value)) => nameSet.find(_.toString().equals(varName.value)).get -> value
    }
  }

  /** Finds multiple definitions in one tree traversal, ordered with the order in the list
    * @param tree
    *   The tree to search in
    * @param nameSet
    *   The set of names to search for
    * @return
    *   A list of the definitions in the order of the names
    */
  /* Since findDefinitions take a set for efficiency, the order is lost. We use this function to restore it.
   * It is still more efficient than passing a list to findDefinitions as here we have an algorithm that depends on the
   * number of variables to find definitions, not on the number of lookups */
  def findDefinitionsOrdered(tree: Tree, nameSet: List[Term]): List[Any] = {
    findDefinitions(tree, nameSet.toSet).sortBy { case (term, _) => nameSet.indexOf(term) }.map { case (_, value) => value }
  }

  // Function that finds the actual symbol of a term. There are cases where we are treating the companion object, e.g.
  // a call to List(1,2,3).head will be on the companion object. It turns out that the Companion object does not
  // inherit from the same parents as the class itself, so we need to resolve the symbol to class symbol.
  // Companion objects generally inherit from AnyRef *and* a class that has as type parameter the class that we're
  // interested in. e.g. the object List inherits from StrictOptimizedSeqFactory[List], or the object Seq inherits
  // from Delegate[Seq].
  // We say "generally" here since Scalameta simply does not guarantee that the hierarchy will be the same as in the
  // code. For some reason, Seq does not inherit from AnyRef in Scalameta, even though it does in the code.
  // Companion objects are differentiable from lists by their symbol, since they are not stored in a variable, they
  // won't have a ValueSignature i.e. getSymbol will return None. They instead have a MethodSignature, from which
  // we can extract the return type (i.e. the companion object itself), the parents and the type parameters.
  // The method works as follows: we first get the symbol of the term. If it is a companion object, we get the parents
  // of the companion object. We then find a parent that is not AnyRef, and get the type parameter of that parent.
  // This will give us the class.
  // If it directly the class, getType will not return None but the class itself and we don't have to do anything.
  private def resolveSymbol(child: Term)(implicit doc: SemanticDocument): Symbol = {
    getType(child) match {
      case Symbol.None =>
        child.symbol.info.flatMap(x =>
          x.signature match {
            case MethodSignature(_, _, SingleType(_, symbol)) => // Companion object, extract method signature
              symbol.info.flatMap(x =>
                x.signature match {
                  case ClassSignature(_, parents, _, _) => // Extract class signature from return type
                    parents.find(_.toString != "AnyRef").collect { // Find parent that is not AnyRef
                      case TypeRef(_, _, List(typeArg: TypeRef)) => typeArg.symbol // Extract type parameter, i.e. class
                    }
                  case _ => None
                }
              )
            case MethodSignature(_, _, TypeRef(ThisType(symbol), _, _)) => Some(symbol)
            case _                                                      => None
          }
        ).getOrElse(Symbol.None)
      case symbol => symbol // Class, do nothing
    }
  }

  /** Check if a symbol inherits from a parent type
    * @param child
    *   The symbol to check
    * @param parent
    *   The parent type to check for
    * @param doc
    *   The document to get the semantic information from
    * @return
    *   Whether or not the symbol inherits from the parent type
    */
  // Simply recurses through the parent hierarchy to find the parent we're looking for.
  def inheritsFrom(child: Symbol, parent: String)(implicit doc: SemanticDocument): Boolean = {
    SymbolMatcher.normalized(parent).matches(child) || // We found a match a this step
    (child.info match { // Or we recurse
      case Some(info) =>
        info.signature match {
          case ClassSignature(_, parents, _, _) => // Extract parents and recurse
            parents.collect { case t: TypeRef => t.symbol }.exists(inheritsFrom(_, parent))
          case TypeSignature(_, _, TypeRef(_, symbol, _)) => // Recurse on upper bound
            inheritsFrom(symbol, parent)
          case _ => false
        }
      case None => false
    })
  }

  /** Check if a term inherits from a parent type. Tries to automatically find the symbol of the child. Prefer using the symbol version if possible.
    * @param child
    *   The term to check
    * @param parent
    *   The parent type to check for
    * @param doc
    *   The document to get the semantic information from
    * @return
    *   Whether or not the term inherits from the parent type
    */
  // Alternative which automatically resolves the symbol of the child
  def inheritsFrom(child: Term, parent: String)(implicit doc: SemanticDocument): Boolean = {
    inheritsFrom(resolveSymbol(child), parent)
  }

  // Version that resolves both symbols
  /** Check if a term inherits from a parent symbol. Tries to automatically find the parent and child symbols. Prefer using the version that takes a parent as a string if you know the parent symbol.
    * @param child
    *   The term to check
    * @param parent
    *   The parent term to check for
    * @param doc
    *   The document to get the semantic information from
    * @return
    *   Whether or not the term inherits from the parent symbol
    */
  def inheritsFrom(child: Term, parent: Term)(implicit doc: SemanticDocument): Boolean = {
    inheritsFrom(resolveSymbol(child), resolveSymbol(parent).value)
  }

  def getTypeArgs(term: Term)(implicit doc: SemanticDocument): List[Symbol] = {
    term.symbol.info match {
      case Some(symInfo) => symInfo.signature match {
          case ValueSignature(TypeRef(_, _, typeArguments)) => typeArguments.collect { case t: TypeRef => t.symbol }
          case _                                            => List()
        }
      case _ => List()
    }
  }

  def litToSymbol(lit: Lit): Symbol = {
    lit match {
      case Lit.String(_)  => Symbol("scala/Predef.String#")
      case Lit.Int(_)     => Symbol("scala/Int#")
      case Lit.Double(_)  => Symbol("scala/Double#")
      case Lit.Float(_)   => Symbol("scala/Float#")
      case Lit.Long(_)    => Symbol("scala/Long#")
      case Lit.Boolean(_) => Symbol("scala/Boolean#")
      case Lit.Null()     => Symbol("scala/Null#")
      case Lit.Unit()     => Symbol("scala/Unit#")
      case Lit.Byte(_)    => Symbol("scala/Byte#")
      case Lit.Short(_)   => Symbol("scala/Short#")
      case Lit.Char(_)    => Symbol("scala/Char#")
      case Lit.Symbol(_)  => Symbol("scala/Symbol#")
      case _              => Symbol.None
    }
  }

}
