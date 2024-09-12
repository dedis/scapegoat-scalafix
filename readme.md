# Scapegoat-Scalafix
## Scalafix implementation of Scapegoat linter for Scala 3

## Versions:

* **Scala 3**:
[![maven](https://img.shields.io/maven-central/v/io.github.dedis/scapegoat-scalafix_3)](https://search.maven.org/artifact/io.github.dedis/scapegoat-scalafix_3)


* **Scala 2.13**:
[![maven](https://img.shields.io/maven-central/v/io.github.dedis/scapegoat-scalafix_2.13)](https://search.maven.org/artifact/io.github.dedis/scapegoat-scalafix_2.13)

## Description
This project is a Scalafix implementation of the Scapegoat linter for Scala 3. It contains a set of rules that can be run on Scala code to detect potential issues and bad practices. The rules are based on the Scapegoat linter for Scala 2, but have been adapted to work with Scalafix and Scala 3.

For now, this project has 52 rules but more are being worked on.
You can track the progress [here](https://docs.google.com/spreadsheets/d/1XovJJg3EInQFFL1-tpqGxpP2O4RFzCOF7zDalQIeB7E/edit?usp=sharing).

## Installation

To install the rules, you first should have the Scalafix plugin installed.
You can install it by adding this line to `project/plugins.sbt`:
```
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.12.1")
```

Then, to obtain this rule set, simply add the following line to your `build.sbt` file:
```
ThisBuild / scalafixDependencies += "io.github.dedis" %% "scapegoat-scalafix" % "1.1.3"
```

**The rules are compatible with Scala 2.13 and Scala 3 (tested for Scala 3.3.1).**

To check proper installation, run `scalafix OptionGet` which should execute the OptionGet rule and succeed if everything went well.

### Note
You might need to add the following lines:
```
inThisBuild(
    List(
        semanticdbEnabled := true,
        semanticdbVersion := scalafixSemanticdb.revision
    )
)
```

This is necessary to enable the SemanticDB, which is required for the rules to work. Only add these lines if SemanticDB is not already enabled in the `build.sbt`.

## Rule list
| Name                               |Brief Description                                                                                                                        |Default Level|
|------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|-------------|
| ArrayEquals                        |Checks for comparison of arrays using `==` which will always return false                                                                |Info         |
| ArraysInFormat                     |Checks for arrays passed to String.format                                                                                                |Error        |
| ArraysToString                     |Checks for explicit toString calls on arrays                                                                                             |Warning      |
| AsInstanceOf                       |Checks for use of `asInstanceOf`                                                                                                         |Warning      |
| AvoidSizeEqualsZero                |Traversable.size can be slow for some data structure, prefer .isEmpty                                                                    |Warning      |
| AvoidSizeNotEqualsZero             |Traversable.size can be slow for some data structure, prefer .nonEmpty                                                                   |Warning      |
| AvoidToMinusOne                    |Checks for loops that use `x to n-1` instead of `x until n`                                                                              |Info         |
| BigDecimalDoubleConstructor        |Checks for use of `BigDecimal(double)` which can be unsafe                                                                               |Warning      |
| BigDecimalScaleWithoutRoundingMode |`setScale()` on a `BigDecimal` without setting the rounding mode can throw an exception                                                  |Warning      |
| BooleanParameter                   |Checks for functions that have a Boolean parameter                                                                                       |Info         |
| BoundedByFinalType                 |Looks for types with upper bounds of a final type                                                                                        |Warning      |
| BrokenOddness                      |Checks for a % 2 == 1 for oddness because this fails on negative numbers                                                                 |Warning      |
| CatchException                     |Checks for try blocks that catch Exception                                                                                               |Warning      |
| CatchExceptionImmediatelyRethrown  |Checks for try-catch blocks that immediately rethrow caught exceptions.                                                                  |Warning      |
| CatchFatal                         |Checks for try blocks that catch fatal exceptions: VirtualMachineError, ThreadDeath, InterruptedException, LinkageError, ControlThrowable|Warning      |
| CatchNpe                           |Checks for try blocks that catch null pointer exceptions                                                                                 |Error        |
| CatchThrowable                     |Checks for try blocks that catch Throwable                                                                                               |Warning      |
| ClassNames                         |Ensures class names adhere to the style guidelines                                                                                       |Info         |
| CollectionIndexOnNonIndexedSeq     |Checks for indexing on a Seq which is not an IndexedSeq                                                                                  |Warning      |
| CollectionNamingConfusion          |Checks for variables that are confusingly named                                                                                          |Info         |
| CollectionNegativeIndex            |Checks for negative access on a sequence eg `list.get(-1)`                                                                               |Warning      |
| CollectionPromotionToAny           |Checks for collection operations that promote the collection to `Any`                                                                    |Warning      |
| ComparingFloatingPointTypes        |Checks for equality checks on floating point types                                                                                       |Error        |
| ComparisonToEmptyList              |Checks for code like `a == List()` or `a == Nil`                                                                                         |Info         |
| ComparisonToEmptySet               |Checks for code like `a == Set()` or `a == Set.empty`                                                                                    |Info         |
| ComparisonWithSelf                 |Checks for equality checks with itself                                                                                                   |Warning      |
| ConstantIf                         |Checks for code where the if condition compiles to a constant                                                                            |Warning      |
| DivideByOne                        |Checks for divide by one, which always returns the original value                                                                        |Warning      |
| DoubleNegation                     |Checks for code like `!(!b)`                                                                                                             |Info         |
| DuplicateImport                    |Checks for import statements that import the same selector                                                                               |Info         |
| DuplicateMapKey                    |Checks for duplicate key names in Map literals                                                                                           |Warning      |
| DuplicateSetValue                  |Checks for duplicate values in set literals                                                                                              |Warning      |
| EitherGet                          |Checks for use of .get on Left or Right                                                                                                  |Error        |
| EmptyCaseClass                     |Checks for case classes like `case class Faceman()`                                                                                      |Info         |
| EmptyFor                           |Checks for empty `for` loops                                                                                                             |Warning      |
| EmptyIfBlock                       |Checks for empty `if` blocks                                                                                                             |Warning      |
| EmptyInterpolatedString            |Looks for interpolated strings that have no arguments                                                                                    |Error        |
| EmptyMethod                        |Looks for empty methods                                                                                                                  |Warning      |
| EmptySynchronizedBlock             |Looks for empty synchronized blocks                                                                                                      |Warning      |
| EmptyTryBlock                      |Looks for empty try blocks                                                                                                               |Warning      |
| EmptyWhileBlock                    |Looks for empty while loops                                                                                                              |Warning      |
| FinalizerWithoutSuper              |Checks for overridden finalizers that do not call super                                                                                  |Warning      |
| IllegalFormatString                |Looks for invalid format strings                                                                                                         |Error        |
| ImpossibleOptionSizeCondition      |Checks for code like `option.size > 2` which can never be true                                                                           |Error        |
| IncorrectNumberOfArgsToFormat      |Checks for wrong number of arguments to `String.format`                                                                                  |Error        |
| IncorrectlyNamedExceptions         |Checks for exceptions that are not called *Exception and vice versa                                                                      |Error        |
| InterpolationToString              |Checks for string interpolations that have .toString in their arguments                                                                  |Warning      |
| LonelySealedTrait                  |Checks for sealed traits which have no implementation                                                                                    |Error        |
| LooksLikeInterpolatedString        |Finds strings that look like they should be interpolated but are not                                                                     |Warning      |
| MapGetAndGetOrElse                 |`Map.get(key).getOrElse(value)` can be replaced with `Map.getOrElse(key, value)`                                                         |Error        |
| MethodReturningAny                 |Checks for defs that are defined or inferred to return `Any`                                                                             |Warning      |
| NanComparison                      |Checks for `x == Double.NaN` which will always fail                                                                                      |Error        |
| NullAssignment                     |Checks for use of `null` in assignments                                                                                                  |Warning      |
| NullParameter                      |Checks for use of `null` in method invocation                                                                                            |Warning      |
| OptionGet                          |Checks for `Option.get`                                                                                                                  |Error        |
| OptionSize                         |Checks for `Option.size`                                                                                                                 |Error        |
| RepeatedCaseBody                   |Checks for case statements which have the same body                                                                                      |Warning      |
| RepeatedIfElseBody                 |Checks for the main branch and the else branch of an `if` being the same                                                                 |Warning      |
| StripMarginOnRegex                 |Checks for .stripMargin on regex strings that contain a pipe                                                                             |Error        |
| SwallowedException                 |Finds catch blocks that don't handle caught exceptions                                                                                   |Warning      |
| TryGet                             |Checks for use of `Try.get`                                                                                                              |Error        |
| UnnecessaryConversion              |Checks for unnecessary `toInt` on instances of Int or `toString` on Strings, etc.                                                        |Warning      |
| UnreachableCatch                   |Checks for catch clauses that cannot be reached                                                                                          |Warning      |
| UnsafeContains                     |Checks for `List.contains(value)` for invalid types                                                                                      |Error        |
| UnsafeStringContains               |Checks for `String.contains(value)` for invalid types                                                                                    |Error        |
| UnsafeTraversableMethods           |Check unsafe traversable method usages (head, tail, init, last, reduce, reduceLeft, reduceRight, max, maxBy, min, minBy)                 |Error        |
| UnusedMethodParameter              |Checks for unused method parameters                                                                                                      |Warning      |
| VarCouldBeVal                      |Checks for `var`s that could be declared as `val`s                                                                                       |Warning      |
| VariableShadowing                  |Checks for multiple uses of the variable name in nested scopes                                                                           |Warning      |
| WhileTrue                          |Checks for code that uses a `while(true)` or `do { } while(true)` block.                                                                 |Warning      |


## Usage

After installation, to run any of these rules, simply call:
```
sbt scalafix RuleName
```

You can also create a `.scalafix.conf` file and enable rules in them. Here is an example with all of the rules enabled:
```
rules = [
    ArrayEquals,
    ArraysInFormat,
    ArraysToString,
    AsInstanceOf,
    AvoidSizeEqualsZero,
    AvoidSizeNotEqualsZero,
    AvoidToMinusOne,
    BigDecimalDoubleConstructor,
    BigDecimalScaleWithoutRoundingMode,
    BooleanParameter,
    BoundedByFinalType,
    BrokenOddness,
    CatchException,
    CatchExceptionImmediatelyRethrown,
    CatchFatal,
    CatchNpe,
    CatchThrowable,
    ClassNames,
    CollectionIndexOnNonIndexedSeq,
    CollectionNamingConfusion,
    CollectionNegativeIndex,
    CollectionPromotionToAny,
    ComparingFloatingPointTypes,
    ComparisonToEmptyList,
    ComparisonToEmptySet,
    ComparisonWithSelf,
    ConstantIf,
    DivideByOne,
    DoubleNegation,
    DuplicateImport,
    DuplicateMapKey,
    DuplicateSetValue,
    EitherGet,
    EmptyCaseClass,
    EmptyFor,
    EmptyIfBlock,
    EmptyInterpolatedString,
    EmptyMethod,
    EmptySynchronizedBlock,
    EmptyTryBlock,
    EmptyWhileBlock,
    FinalizerWithoutSuper,
    IllegalFormatString,
    ImpossibleOptionSizeCondition,
    IncorrectNumberOfArgsToFormat,
    IncorrectlyNamedExceptions,
    InterpolationToString,
    LonelySealedTrait,
    LooksLikeInterpolatedString,
    MapGetAndGetOrElse,
    MethodReturningAny,
    NanComparison,
    NullAssignment,
    NullParameter,
    OptionGet,
    OptionSize,
    RepeatedCaseBody,
    RepeatedIfElseBody,
    StripMarginOnRegex,
    SwallowedException,
    TryGet,
    UnnecessaryConversion,
    UnreachableCatch,
    UnsafeContains,
    UnsafeStringContains,
    UnsafeTraversableMethods,
    UnusedMethodParameter,
    VarCouldBeVal,
    VariableShadowing,
    WhileTrue
]
```

With this, you can simply run all the rules in the configuration file by calling:
```
sbt scalafix
```

## Usage

To run the rules, simply execute the following command:
```
sbt scalafix
```

## Modifying and adding rules

Scalafix provides some documentation on how to write rules, following their [tutorial](https://scalacenter.github.io/scalafix/docs/developers/tutorial.html) is recommended.

To add a rule, you need to
* Create rule test cases in the `input/src/main/scala/fix` folder
* Create the rule in the `rules/src/main/scala/fix` folder
* Add the rule to the list of rules in `rules/src/main/resources/META-INF/services/scalafix.v1.ScalafixRule`


Output folder is ignored since this is a linter.

## Testing

To test the rules run:
```
sbt test
```

## Publishing

To publish the rules, follow the tutorial on the Scalafix website.
In short you need to:
* Generate a gpg key to sign
* Publish the GPG key to https://keyserver.ubuntu.com
* Create an account on Maven central (see [tutorial](https://central.sonatype.org/register/central-portal/#choosing-a-namespace))
* Verify namespace access for io.github.dedis (see [tutorial](https://central.sonatype.org/register/namespace/#create-an-account))
* Modify the version in `build.sbt`
* Run `sbt publishSigned`
* ZIP the `target/sonatype-staging/VERSION/io/` folder (only include starting from io) and publish it to Maven:
simply click "Publish Component" on Sonatype, set the name to "io.github.dedis:scapegoat-scalafix:VERSION" and upload the ZIP file.

_Don't forget to update the version in the installation section_


