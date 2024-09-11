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

## Installation

To install the rules, simply add the following to your `build.sbt` file:
```
ThisBuild / scalafixDependencies += "io.github.dedis" %% "scapegoat-scalafix" % "1.1.2"
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
|Name                        |Description                                                                                                                              |Level  |
|----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|-------|
|ArraysInFormat              |Checks for arrays passed to String.format                                                                                                |Error  |
|CatchNpe                    |Checks for try blocks that catch null pointer exceptions                                                                                 |Error  |
|ComparingFloatingTypes      |Checks for equality checks on floating point types                                                                                       |Error  |
|EitherGet                   |Checks for use of .get on Left or Right                                                                                                  |Error  |
|EmptyInterpolatedString     |Looks for interpolated strings that have no arguments                                                                                    |Error  |
|IllegalFormatString         |Looks for invalid format strings                                                                                                         |Error  |
|ImpossibleOptionSizeCondition |Checks for code like option.size > 2 which can never be true                                                                             |Error  |
|IncorrectNumberOfArgsToFormat |Checks for wrong number of arguments to String.format                                                                                    |Error  |
|IncorrectlyNamedExceptions  |Checks for exceptions that are not called *Exception and vice versa                                                                      |Error  |
|LonelySealedTrait           |Checks for sealed traits which have no implementation                                                                                    |Error  |
|MapGetAndGetOrElse          |Map.get(key).getOrElse(value) can be replaced with Map.getOrElse(key,value)                                                              |Error  |
|NanComparison               |Checks for x == Double.NaN which will always fail                                                                                        |Error  |
|OptionGet                   |Checks for Option.get                                                                                                                    |Error  |
|OptionSize                  |Checks for Option.size                                                                                                                   |Error  |
|StripMarginOnRegex          |Checks for .stripMargin on regex strings that contain a pipe                                                                             |Error  |
|TryGet                      |Checks for use of Try.get                                                                                                                |Error  |
|UnsafeContains              |Checks for List.contains(value) for invalid types                                                                                        |Error  |
|UnsafeStringContains        |Checks for String.contains(value) for invalid types                                                                                      |Error  |
|UnsafeTraversableMethods    |Checks unsafe traversable method usages (head, tail, init, last, reduce, reduceLeft, reduceRight, max, maxBy, min, minBy)                |Error  |
|AvoidSizeEqualsZero         |Traversable.size can be slow for some data structure, prefer .isEmpty                                                                    |Warning|
|AvoidSizeNotEqualsZero      |Traversable.size can be slow for some data structure, prefer .nonEmpty                                                                   |Warning|
|CatchException              |Checks for try blocks that catch Exception                                                                                               |Warning|
|CatchExceptionImmediatelyRethrown|Checks for try-catch blocks that immediately rethrow caught exceptions.                                                                  |Warning|
|CatchFatal                  |Checks for try blocks that catch fatal exceptions: VirtualMachineError, ThreadDeath, InterruptedException, LinkageError, ControlThrowable|Warning|
|CatchThrowable              |Checks for try blocks that catch Throwable                                                                                               |Warning|
|CollectionIndexOnNonIndexedSeq |Checks for indexing on a Seq which is not an IndexedSeq                                                                                  |Warning|
|CollectionNegativeIndex     |Checks for negative access on a sequence e.g. list.get(-1)                                                                               |Warning|
|CollectionPromotionToAny    |Checks for collection operations that promote the collection to Any                                                                      |Warning|
|ComparisonWithSelf          |Checks for equality checks with itself                                                                                                   |Warning|
|ConstantIf                  |Checks for code where the if condition compiles to a constant                                                                            |Warning|
|DuplicateImport             |Checks for import statements that import the same selector                                                                               |Info   |
|EmptyFor                    |Checks for empty for loops                                                                                                               |Warning|
|EmptyIfBlock                |Checks for empty if blocks                                                                                                               |Warning|
|EmptyMethod                 |Looks for empty methods                                                                                                                  |Warning|
|EmptySynchronizedBlock      |Looks for empty synchronized blocks                                                                                                      |Warning|
|EmptyTryBlock               |Looks for empty try blocks                                                                                                               |Warning|
|EmptyWhileBlock             |Looks for empty while loops                                                                                                              |Warning|
|FinalizerWithoutSuper       |Checks for overriden finalizers that do not call super                                                                                   |Warning|
|LooksLikeInterpolatedString |Finds strings that look like they should be interpolated but are not                                                                     |Warning|
|MethodReturningAny          |Checks for defs that are defined or inferred to return Any                                                                               |Warning|
|NullAssignment              |Checks for use of null in assignments                                                                                                    |Warning|
|NullParameter               |Checks for use of null in method invocation                                                                                              |Warning|
|RepeatedCaseBody            |Checks for case statements which have the same body                                                                                      |Warning|
|RepeatedIfElseBody          |Checks for the main branch and the else branch of an if being the same                                                                   |Warning|
|SwallowedException          |Find catch blocks that don't handle caught exceptions                                                                                    |Warning|
|UnnecessaryConversion       |Checks for unnecessary toInt on instances of Int or toString on Strings, etc.                                                            |Warning|
|UnreachableCatch            |Checks for catch clauses that cannot be reached                                                                                          |Warning|
|UnusedMethodParameter       |Checks for unused method parameters                                                                                                      |Warning|
|VarCouldBeVal               |Checks for vars that could be declared as vals                                                                                           |Warning|
|VariableShadowing           |Checks for multiple uses of the variable name in nested scopes                                                                           |Warning|
|While true                  |Checks for code that uses a while(true) or do {} while(true) block                                                                       |Warning|
|InterpolationToString       |Checks for string interpolations that have .toString in their arguments                                                                  |Warning|


## Usage

After installation, to run any of these rules, simply call:
```
sbt scalafix RuleName
```

You can also create a `.scalafix.conf` file and enable rules in them. Here is an example with all of the rules enabled:
```
rules = [
    ArraysInFormat,
    CatchNpe,
    ComparingFloatingPointTypes,
    EitherGet,
    EmptyInterpolatedString,
    IllegalFormatString,
    ImpossibleOptionSizeCondition,
    IncorrectNumberOfArgsToFormat,
    IncorrectlyNamedExceptions,
    LonelySealedTrait,
    MapGetAndGetOrElse,
    NanComparison,
    OptionGet,
    OptionSize,
    StripMarginOnRegex,
    TryGet,
    UnsafeContains,
    UnsafeStringContains,
    UnsafeTraversableMethods,
    AvoidSizeEqualsZero,
    AvoidSizeNotEqualsZero,
    CatchException,
    CatchExceptionImmediatelyRethrown,
    CatchFatal,
    CatchThrowable,
    CollectionIndexOnNonIndexedSeq,
    CollectionNegativeIndex,
    CollectionPromotionToAny,
    ComparisonWithSelf,
    ConstantIf,
    DuplicateImport,
    EmptyFor,
    EmptyIfBlock,
    EmptyMethod,
    EmptySynchronizedBlock,
    EmptyTryBlock,
    EmptyWhileBlock,
    FinalizerWithoutSuper,
    LooksLikeInterpolatedString,
    MethodReturningAny,
    NullAssignment,
    NullParameter,
    RepeatedCaseBody,
    RepeatedIfElseBody,
    SwallowedException,
    UnnecessaryConversion,
    UnreachableCatch,
    UnusedMethodParameter,
    VarCouldBeVal,
    VariableShadowing,
    WhileTrue,
    InterpolationToString,
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


