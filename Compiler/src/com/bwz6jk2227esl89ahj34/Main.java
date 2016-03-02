package com.bwz6jk2227esl89ahj34;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    private static String sourcePath = "./";
    private static String diagnosticPath = "./";
    private static String libPath = "./";
    private static boolean debug;

    public static void main(String[] args) {
        CLI cli = new CLI();
        /*
            The order in which these options are added is the same as which
            they will be executed (but options can be provided in any order
            when calling xic
         */
        cli.addOption("--debug",
                      "Turns on debug mode.",
                      Main::turnDebugOn,
                      0);
        cli.addOption("-sourcepath",
                "Set the path for source files. Takes one argument.",
                Main::setSourcePath,
                1);
        cli.addOption("-D",
                "Set the path for diagnostic files. Takes one argument.",
                Main::setDiagnosticPath,
                1);
        cli.addOption("-libpath",
                "Set the path for interface files. Takes one argument.",
                Main::setLibPath,
                1);
        cli.addOption("--lex",
                "Lex the .xi source files to .lexed files.",
                files -> Lexer.lexFile(sourcePath, diagnosticPath, files),
                0);
        cli.addOption("--parse",
                "Parse the .xi source files to .parsed files.",
                files -> Parser.parseFile(sourcePath, diagnosticPath, files),
                0);
        cli.addOption("--typecheck",
                "Typecheck the .xi source files",
                files -> Arrays.stream(files).forEach(file ->
                                Util.typeCheck(sourcePath, diagnosticPath,
                                        libPath, file)),
                0);
        cli.execute(args);

        if(debug) {
            // put debug mode behaviors here
            //parseTestHarness();
            typeCheckTests();
        }
    }

    /**
     * Sets the path for source files
     * @param args single element String array containing the path
     */
    public static void setSourcePath(String[] args) {
        if (args.length == 0 || args[0] == null) {
            System.out.println("Please provide source path");
            return;
        }
        sourcePath = args[0] + "/";
    }

    /**
     * Sets the path for diagnostic files
     * @param args single element String array containing the path
     */
    public static void setDiagnosticPath(String[] args) {
        if (args.length == 0 || args[0] == null) {
            System.out.println("Please provide diagnostic path");
            return;
        }
        diagnosticPath = args[0] + "/";
    }

    public static void setLibPath(String[] args) {
        if (args.length == 0 || args[0] == null) {
            System.out.println("Please provide lib path");
            return;
        }
        libPath = args[0] + "/";
    }

    /**
     * Turns on debug mode, which runs some automated tests and provides
     * more print statements.
     */
    public static void turnDebugOn(String[] args) {
        debug = true;
    }

    public static boolean debugOn() {
        return debug;
    }

    /**
     * Automated tests for typecheck.
     */
    public static void typeCheckTests() {

        System.out.println("================Passed Tests================");
        for (String filename : Util.getDirectoryFiles("typecheck/passtests/")) {
            Util.typeCheck("typecheck/passtests/", "typecheck/passtests/",
                    "typecheck/lib/", filename);
        }

        System.out.println("================Failed Tests================");
        for (String filename : Util.getDirectoryFiles("typecheck/failtests/")) {
            Util.typeCheck("typecheck/failtests/", "typecheck/failtests/",
                    "typecheck/lib/", filename);
        }
    }

    /**
     * Executes parseFile on a list of filenames. For debugging purposes only.
     * @throws IOException
     */
    public static void parseTestHarness() throws IOException {
        String[] testFileNames = new String[] {"arrayinit", "arrayinit2",
                "ex1", "ex2", "gcd", "insertionsort", "mdarrays", "ratadd",
                "ratadduse", "spec1", "spec2", "spec3" };

        for (int i = 0; i < testFileNames.length; i++) {
            testFileNames[i] = testFileNames[i] + ".xi";
        }

        Parser.parseFile("parser/tests/", "parser/tests/", testFileNames);

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
