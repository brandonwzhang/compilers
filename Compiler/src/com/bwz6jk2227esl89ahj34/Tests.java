package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.Program;
import com.bwz6jk2227esl89ahj34.AST.parse.Lexer;
import com.bwz6jk2227esl89ahj34.AST.parse.Parser;
import com.bwz6jk2227esl89ahj34.AST.visit.ConstantFoldingVisitor;
import com.bwz6jk2227esl89ahj34.AST.visit.NodeVisitor;
import com.bwz6jk2227esl89ahj34.AST.visit.PrintVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.CodeWriterSExpPrinter;
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
     * Compares the output of executable to IR simulation
     */
    public static void regressionTest() {
        List<String> files = Util.getDirectoryFiles("./").stream()
                .filter(filename -> filename.contains(".xi"))
                .filter(filename -> !excluded(filename))
                .collect(Collectors.toList());
        List<String> results = new LinkedList<>();
        for (String file : files) {
            String[] irCommand = {"../xic", "-libpath", "../lib", "-D", "irtests", "--irrun", file};
            Core.generateAssembly("./", "./", "../lib/", "assemblytests", file);
            String[] assemblyCommand = {"./" + file.replace(".xi", "")};
            if (Util.runCommand(irCommand).equals(Util.runCommand(assemblyCommand))) {
                results.add(file + ": passed");
            } else {
                results.add(file + ": failed");
            }
        }
        for (String result : results) {
            System.out.println(result);
        }
    }

    /**
     * Returns true if the given file is excluded from testing. Assumes that
     * the given string ends in .xi
     */
    public static boolean excluded(String file) {
        return exclude.contains(file.substring(0, file.length() - 3));
    }

    /**
     * Automated tests for interpreted the generated IR.
     */
    public static void irRunTests() {
        System.out.println("\n================IR RUN TESTS================");

        Util.getDirectoryFiles("ir/irrun/").stream()
                .filter(filename -> filename.contains(".xi"))
                .filter(filename -> !excluded(filename))
                .forEach(filename -> Core.irRun("ir/irrun/",
                        "ir/irrun/diagnostics/", "ir/lib/", filename));
    }

    /**
     * Automated tests for MIR generation.
     */
    public static void mirGenTests() {
        System.out.println("\n================MIR GEN TESTS================");

        Util.getDirectoryFiles("ir/irgen/").stream()
                .filter(filename -> filename.contains(".xi"))
                .filter(filename -> !excluded(filename))
                .forEach(filename -> Core.mirGen("ir/irgen/",
                        "ir/irgen/diagnostics/mir/", "ir/lib/", filename, false));
    }

    /**
     * Automated tests for IR generation.
     */
    public static void irGenTests() {
        System.out.println("\n================IR GEN TESTS================");

        Util.getDirectoryFiles("ir/irgen/").stream()
                .filter(filename -> filename.contains(".xi"))
                .filter(filename -> !excluded(filename))
                .forEach(filename -> Core.irGen("ir/irgen/",
                        "ir/irgen/diagnostics/ir/", "ir/lib/", filename, true, false));
    }


    public static void constantFoldTests() {
        System.out.println("\n==CONSTANT FOLD TESTS==");

        Util.getDirectoryFiles("constantfold/").stream()
                .filter(filename -> filename.contains(".xi"))
                .forEach(Tests::constantFoldHelper);
    }

    /**
     * Prints the result of constant folding (before IR translation)
     * on a single Xi program.
     */
    public static void constantFoldHelper(String filename) {

        Optional<FileReader> reader = Util.getFileReader("constantfold/", filename);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);
        List<String> lines = new ArrayList<>();
        Optional<Program> program = Core.typeCheckHelper(parser, "constantfold/", lines, false);
        if (!program.isPresent()) {
            return;
        }

        ConstantFoldingVisitor cfv = new ConstantFoldingVisitor();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodeWriterSExpPrinter printer =
                new CodeWriterSExpPrinter(baos);
        NodeVisitor pv = new PrintVisitor(printer);

        program.get().accept(cfv);
        program.get().accept(pv);
        printer.flush();
        System.out.println(baos.toString().replaceAll("\\s+", " ")
                .replaceAll("\\(\\s?", "(")
                .replaceAll("\\s?\\)", ")")
                .trim());
    }

    /**
     * Automated tests for typecheck.
     */
    public static void typeCheckTests() {
        System.out.println("\n================Typecheck Tests================");

        System.out.println("\n================Passed Tests================");
        Util.getDirectoryFiles("typecheck/passtests/").stream()
                .filter(filename -> filename.contains(".xi"))
                .forEach(filename -> Core.typeCheck("typecheck/passtests/",
                        "typecheck/passtests/diagnostics/",
                        "typecheck/lib/", filename));

        System.out.println("\n================Failed Tests================");
        Util.getDirectoryFiles("typecheck/failtests/").stream()
                .filter(filename -> filename.contains(".xi"))
                .forEach(filename -> Core.typeCheck("typecheck/failtests/",
                        "typecheck/failtests/diagnostics/",
                        "typecheck/lib/", filename));

    }

    /**
     * Executes parseFile on a list of filenames. For debugging purposes only.
     * @throws IOException
     */
    public static void parseTests() throws IOException {
        String[] testFileNames = new String[] {"arrayinit", "arrayinit2",
                "ex1", "ex2", "gcd", "insertionsort", "mdarrays", "ratadd",
                "ratadduse", "spec1", "spec2", "spec3" };

        for (int i = 0; i < testFileNames.length; i++) {
            testFileNames[i] = testFileNames[i] + ".xi";
            Core.parseFile("parser/tests/", "parser/tests/", testFileNames[i]);
        }

        for (String file : testFileNames) {
            String sExpFile1 =
                    "parser/tests/" + file.replace(".xi",".parsedsol");
            String sExpFile2 =
                    "parser/tests/" + file.replace(".xi", ".parsed");
            System.out.println();
            System.out.println(Util.compareSExpFiles(sExpFile1, sExpFile2));
        }
    }
}
