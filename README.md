# Compilers
Compiler for the Xi Programming Language

## Building
With Apache Ant installed and the `JAVA_HOME` environment variable set, run the
`xic-build` script.

## Running
To compile a Xi program:
```
xic <filename>
```
To specify a folder containing interface `.ixi` files:
```
xic -libpath <path> <filename>
```
To view all options:
```
xic --help
```


### Advanced
We use the following optimizations:
* `cse` Common Subexpression Elimination
* `cf` Constant Folding
* `uce` Unreachable Code Elimination
* `dce` Dead Code Elimination
* `cp` Constant Propagation
* `copy` Copy Propagation

To compile with no optimizations, pass `-O`. To compile with only specified
optimizations on, pass `-O<opt>` for each optimization. To turn specified
optimizations off, pass `-O-no-<opt>` for each optimization.

To run our test suite, include the flag `--tests`.
To run in debug mode, include the flag `--debug`.
