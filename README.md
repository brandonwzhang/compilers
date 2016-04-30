# Compilers
Compiler for the Xi Programming Language

## Building
With Apache Ant installed and the `JAVA_HOME` environment variable set, run the `xic-build` script.
```
./xic-build
```


## Running
To compile a Xi program:
```
./xic <filename>
```
To specify a folder containing interface `.ixi` files:
```
./xic -libpath <path> <filename>
```
To view all options:
```
./xic --help
```


### Advanced
To run our test suite, include the flag `--tests`.
To run in debug mode, include the flag `--debug`.
<!--To modify the test mode behaviors, go to the if(tests){} block in Main. These-->
<!--behaviors will run regardless of and in addition to any options that you-->
<!--provide normally.  Test methods will read in all of the .xi files inside their-->
<!--respective directories (for example, irGenTests() will read in all of the Xi-->
<!--files in ir/irrgen).-->


<!--If certain tests are crashing the test suite and you want to exclude them-->
<!--temporarily, add the name of the file (without the file extension) to the-->
<!--line String[] exclude = {};-->
<!--This line is the first line inside the if(tests){} block in Main.-->

<!--In debug mode, you will receive additional print statements to the console-->
<!--that inform you of things like reading and writing files.-->
<!--MIR files (extension .mir) are written.-->

<!--To change irrun to interpret the MIR instead of the LIR,-->
<!--go to the method irRun() in Core and change the line:-->
<!--Optional<FileReader> reader = Util.getFileReader(diagnosticPath, file.substring(0, file.length() - 2) + "ir");-->
<!--to:-->
<!--Optional<FileReader> reader = Util.getFileReader(diagnosticPath, file.substring(0, file.length() - 2) + "mir");-->
<!--Note: if you do this, you will probably want to run debug mode so that the .mir files actually get written.-->
