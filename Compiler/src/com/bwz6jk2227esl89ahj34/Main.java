package com.bwz6jk2227esl89ahj34;

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
                files -> Arrays.stream(files).forEach(file ->
                        Core.lexFile(sourcePath, diagnosticPath,
                                file)),
                0);
        cli.addOption("--parse",
                "Parse the .xi source files to .parsed files.",
                files -> Arrays.stream(files).forEach(file ->
                        Core.parseFile(sourcePath, diagnosticPath,
                                file)),
                0);
        cli.addOption("--typecheck",
                "Typecheck the .xi source files",
                files -> Arrays.stream(files).forEach(file ->
                                Core.typeCheck(sourcePath, diagnosticPath,
                                        libPath, file)),
                0);
        cli.addOption("--irgen",
                "Generate intermediate code and write its S-expression representation.",
                files -> Arrays.stream(files).forEach(file ->
                        Core.irGen(sourcePath, diagnosticPath, libPath, file)),
                0);
        cli.addOption("--irrun",
                "Generate and interpret intermediate code",
                files -> Arrays.stream(files).forEach(file ->
                        Core.irRun(sourcePath, diagnosticPath, libPath, file)),
                0);
        cli.execute(args);

        if(debug) { // put debug mode behaviors here
            try {
                //Tests.parseTests();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Tests.typeCheckTests();
            //Tests.constantFoldTests();
            //Tests.irGenTests();
            Tests.mirGenTests();
            //Tests.irRunTests();
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

}
