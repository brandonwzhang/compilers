package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.util.Util;

import java.util.Arrays;
import java.util.Collections;

public class Main {
    private static String sourcePath = "./";
    private static String diagnosticPath = "./";
    private static String libPath = "./";
    private static String assemblyPath = "./";
    private static String target = "linux";
    private static boolean debug;
    private static boolean optimizations = true;
    private static boolean tests;

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
        cli.addOption("--tests",
                      "Runs the test suite.",
                      Main::turnTestsOn,
                      0);
        cli.addOption("-sourcepath",
                      "Set the path for source files. Takes one argument.",
                      path -> setSourcePath(path[0]),
                      1);
        cli.addOption("-D",
                      "Set the path for diagnostic files. Takes one argument.",
                      path -> setDiagnosticPath(path[0]),
                      1);
        cli.addOption("-d",
                      "Specifies where to place the generated assembly files.",
                      path -> setAssemblyPath(path[0]),
                      1);
        cli.addOption("-O",
                      "Turn off optimizations",
                      Main::turnOptimizationsOff,
                      0);
        cli.addOption("-libpath",
                      "Set the path for interface files. Takes one argument.",
                      path -> setLibPath(path[0]),
                      1);
        cli.addOption("-target",
                      "Specify the operating system. Default is linux. " +
                              "windows and macos are not allowed.",
                      path -> setTarget(path[0]),
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
                              Core.irGen(sourcePath, diagnosticPath, libPath, file, true, false)),
                      0);
        cli.addOption("--irrun",
                      "Generate and interpret intermediate code",
                      files -> Arrays.stream(files).forEach(file ->
                              Core.irRun(sourcePath, diagnosticPath, libPath, file)),
                      0);

        cli.execute(args);

        if(tests) { // put debug mode behaviors here

            String[] exclude = {"enigma", "medley01"};
            Collections.addAll(Tests.exclude, exclude);
            System.out.println("\nDEBUG: Excluding: " + Tests.exclude.toString());

            try {
               // Tests.parseTests();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Tests.typeCheckTests();
            //Tests.constantFoldTests();
            //Tests.mirGenTests();
            //Tests.irGenTests();
            //Tests.irRunTests();
            Tests.regressionTest();
        }
    }

    public static void setSourcePath(String path) {
        sourcePath = sourcePath + "/";
    }

    public static void setDiagnosticPath(String path) {
        diagnosticPath = path + "/";
    }

    public static void setAssemblyPath(String path) {
        assemblyPath = path + "/";
    }

    public static void setLibPath(String path) {
        libPath = path + "/";
    }

    public static void setTarget(String path) {
        target = path;
    }

    public static String sourcePath() {
        return sourcePath;
    }

    public static String diagnosticPath() {
        return diagnosticPath;
    }

    public static String libPath() {
        return libPath;
    }

    public static String assemblyPath() {
        return assemblyPath;
    }

    public static String target() {
        return target;
    }

    /**
     * Turns on debug mode, which runs some automated tests and provides
     * more print statements.
     */
    public static void turnDebugOn(String[] args) {
        debug = true;
    }

    /**
     * Turns on optimizations (constant folding).
     */
    public static void turnOptimizationsOff(String[] args) {
        optimizations = false;
    }

    public static void turnTestsOn(String[] args) {
        tests = true;
    }

    public static boolean debugOn() {
        return debug;
    }

    public static boolean optimizationsOn() {
        return optimizations;
    }

}
