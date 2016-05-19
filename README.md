# ogc 
A compiler for the
[Xi](http://www.cs.cornell.edu/courses/cs4120/2016sp/project/oolang.pdf)
Programming Language

In addition to the features mentioned in the language specification, we also
support downcasting `TargetClass#Expression`, `instanceof`, and for-loops.
Example usage of all of these features can be found in `examples/hashtable.xi`.

## Building
With Apache Ant installed and the `JAVA_HOME` environment variable set, run the
`ogc-build` script. If building for the first time, ensure you have an internet
connection.

## Running
To compile a Xi program:
```
ogc <filename>
```
To specify a folder containing interface `.ixi` files:
```
ogc -libpath <path> <filename>
```
To compile and link multiple files, pass all of the files to `ogc`.

To view all options:
```
ogc --help
```


### Advanced
We support the following optimizations:
* `cse` Common Subexpression Elimination
* `cf` Constant Folding
* `uce` Unreachable Code Elimination
* `dce` Dead Code Elimination
* `cp` Constant Propagation
* `copy` Copy Propagation

To compile with no optimizations, pass `-O`. To compile with only specified
optimizations on, pass `-O<opt>` for each optimization. To turn specified
optimizations off, pass `-O-no-<opt>` for each optimization.
