package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.Program;
import com.bwz6jk2227esl89ahj34.ir.IRCompUnit;
import com.bwz6jk2227esl89ahj34.util.Util;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Tests {

    /**
     * A set of files to exclude from testing. It is assumed that entries in this
     * set do not contain a file extension.
     */
    public static Set<String> exclude = new HashSet<>();

    /**
     * Returns true if the given file is excluded from testing. Assumes that
     * the given string ends in .xi
     */
    public static boolean excluded(String file) {
        return exclude.contains(file.substring(0, file.length() - 3));
    }

    /**
     * Compares the output of executable to IR simulation
     * Must be run from the directory containing the xi files
     */
    public static void regressionTest() {
        String target = "linux"; // TODO: change this

        // Get all files in test directory
        List<String> files = Util.getDirectoryFiles(Util.rootPath + "/tests").stream()
                .filter(filename -> filename.contains(".xi"))
                .filter(filename -> !excluded(filename))
                .collect(Collectors.toList());

        Main.setSourcePath(".");
        Main.setDiagnosticPath(".");
        Main.setLibPath(Util.rootPath + "/lib");
        Main.setAssemblyPath(".");

        List<String> results = new LinkedList<>();
        for (String file : files) {
            String fileName = file.substring(0, file.lastIndexOf('.'));

            String[] irCommand = {Util.rootPath + "/xic", "-libpath", Util.rootPath + "/lib", "--irrun", "tests/" + file};
            String[] assemblyCommand = {"./tests/" + fileName};
            // Run the IR and executable and print the outputs
            System.out.println("***************" + file + "***************");
            System.out.println("==================IR==================");
            List<String> irResults = Util.runCommand(irCommand);
            System.out.println("===============Assembly===============");
            List<String> assemblyResults = Util.runCommand(assemblyCommand);

            // Store results
            if (irResults.equals(assemblyResults)) {
                results.add(file + ": passed");
            } else {
                results.add(file + ": failed");
            }

            // Get rid of output files
            try {
                ProcessBuilder rmir = new ProcessBuilder("rm", "tests/" + fileName + ".ir");
                rmir.inheritIO().start().waitFor();
                ProcessBuilder rmassembly = new ProcessBuilder("rm", "tests/" + fileName + ".s");
                rmassembly.inheritIO().start().waitFor();
                ProcessBuilder rmexecutable = new ProcessBuilder("rm", "tests/" + fileName);
                rmexecutable.inheritIO().start().waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Print out results
        for (String result : results) {
            System.out.println(result);
        }
    }

    /**
     * Automated tests for interpreted the generated IR.
     */
    public static void irRunTests() {

        Main.setSourcePath("ir/irrun");
        Main.setDiagnosticPath("ir/irrun/diagnostics");
        Main.setLibPath("ir/lib");
        Main.turnIRRunDiagnosticsOn(null);

        System.out.println("\n================IR RUN TESTS================");

        Util.getDirectoryFiles("ir/irrun/").stream()
                .filter(filename -> filename.contains(".xi"))
                .filter(filename -> !excluded(filename))
                .forEach(filename -> {
                    Optional<Program> program = Core.parseFile(filename);
                    if (!program.isPresent()) {
                        return;
                    }
                    program = Core.typeCheck(filename, program.get());
                    if (!program.isPresent()) {
                        return;
                    }
                    IRCompUnit mirRoot = Core.mirGen(filename, program.get());
                    Core.irGen(filename, mirRoot);
                    Core.irRun(filename);
                });

        Main.turnIRRunDiagnosticsOff();
    }

    /**
     * Automated tests for MIR generation.
     */
    public static void mirGenTests() {
        Main.setSourcePath("ir/irgen");
        Main.setDiagnosticPath("ir/irgen/diagnostics/mir");
        Main.setLibPath("ir/lib");
        Main.turnIRGenDiagnosticsOn(null);
        System.out.println("\n================MIR GEN TESTS================");

        Util.getDirectoryFiles("ir/irgen/").stream()
                .filter(filename -> filename.contains(".xi"))
                .filter(filename -> !excluded(filename))
                .forEach(filename -> {
                    Optional<Program> program = Core.parseFile(filename);
                    if (!program.isPresent()) {
                        return;
                    }
                    program = Core.typeCheck(filename, program.get());
                    if (!program.isPresent()) {
                        return;
                    }
                    Core.mirGen(filename, program.get());
                });

        Main.turnIRGenDiagnosticsOff();
    }

    /**
     * Automated tests for IR generation.
     */
    public static void irGenTests() {
        Main.setSourcePath("ir/irgen");
        Main.setDiagnosticPath("ir/irgen/diagnostics/ir");
        Main.setLibPath("ir/lib");
        Main.turnIRGenDiagnosticsOn(null);

        System.out.println("\n================IR GEN TESTS================");

        Util.getDirectoryFiles("ir/irgen/").stream()
                .filter(filename -> filename.contains(".xi"))
                .filter(filename -> !excluded(filename))
                .forEach(filename -> {
                    Optional<Program> program = Core.parseFile(filename);
                    if (!program.isPresent()) {
                        return;
                    }
                    program = Core.typeCheck(filename, program.get());
                    if (!program.isPresent()) {
                        return;
                    }
                    IRCompUnit mirRoot = Core.mirGen(filename, program.get());
                    Core.irGen(filename, mirRoot);
                });

        Main.turnIRGenDiagnosticsOff();
    }

    /**
     * Automated tests for typecheck.
     */
    public static void typeCheckTests() {

        Main.turnTypeCheckDiagnosticsOn(null);
        Main.setLibPath("typecheck/lib");

        System.out.println("\n================Typecheck Tests================");

        Main.setSourcePath("typecheck/passtests");
        Main.setDiagnosticPath("typecheck/passtests/diagnostics");
        System.out.println("\n================Passed Tests================");
        Util.getDirectoryFiles("typecheck/passtests/").stream()
                .filter(filename -> filename.contains(".xi"))
                .forEach(filename -> {
                    Optional<Program> program = Core.parseFile(filename);
                    if (!program.isPresent()) {
                        return;
                    }
                    Core.typeCheck(filename, program.get());
                });

        Main.setSourcePath("typecheck/failtests");
        Main.setDiagnosticPath("typecheck/failtests/diagnostics");
        System.out.println("\n================Failed Tests================");
        Util.getDirectoryFiles("typecheck/failtests/").stream()
                .filter(filename -> filename.contains(".xi"))
                .forEach(filename -> {
                    Optional<Program> program = Core.parseFile(filename);
                    if (!program.isPresent()) {
                        return;
                    }
                    Core.typeCheck(filename, program.get());
                });

        Main.turnTypeCheckDiagnosticsOff();
    }

    /**
     * Executes parseFile on a list of filenames. For debugging purposes only.
     * @throws IOException
     */
    public static void parseTests() throws IOException {
        String[] testFileNames = new String[] {"arrayinit", "arrayinit2",
                "ex1", "ex2", "gcd", "insertionsort", "mdarrays", "ratadd",
                "ratadduse", "spec1", "spec2", "spec3" };

        Main.setSourcePath("parser/tests");
        Main.setDiagnosticPath("parser/tests");
        Main.turnParseDiagnosticsOn(null);

        for (int i = 0; i < testFileNames.length; i++) {
            testFileNames[i] = testFileNames[i] + ".xi";
            Core.parseFile(testFileNames[i]);
        }

        for (String file : testFileNames) {
            String sExpFile1 =
                    "parser/tests/" + file.replace(".xi", ".parsedsol");
            String sExpFile2 =
                    "parser/tests/" + file.replace(".xi", ".parsed");
            System.out.println();
            System.out.println(Util.compareSExpFiles(sExpFile1, sExpFile2));
        }

        Main.turnParseDiagnosticsOff();
    }
}
