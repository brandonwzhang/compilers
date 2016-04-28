package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.cli.CLI;
import com.bwz6jk2227esl89ahj34.optimization.OptimizationType;

import java.util.*;

public class Main {
    private static String sourcePath = "./";
    private static String diagnosticPath = "./";
    private static String libPath = "./";
    private static String assemblyPath = "./";
    private static String target = "linux";
    private static boolean debug;
    private static boolean tests;
    private static boolean lex;
    private static boolean parse;
    private static boolean typecheck;
    private static boolean irgen;
    private static boolean irrun;
    private static boolean reportInitialIR = false;
    private static boolean reportFinalIR = false;
    private static boolean reportInitialCFG = false;
    private static boolean reportFinalCFG = false;

    public static boolean allOptimizations = true;
    public static HashMap<OptimizationType, Boolean> optimizationMap = new HashMap<>();

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
                      os -> setTarget(os[0]),
                      1);
        cli.addOption("--lex",
                      "Lex the .xi source files to .lexed files.",
                      Main::turnLexDiagnosticsOn,
                      0);
        cli.addOption("--parse",
                      "Parse the .xi source files to .parsed files.",
                      Main::turnParseDiagnosticsOn,
                      0);
        cli.addOption("--typecheck",
                      "Typecheck the .xi source files",
                      Main::turnTypeCheckDiagnosticsOn,
                      0);
        cli.addOption("--irgen",
                      "Generate intermediate code and write its S-expression representation.",
                      Main::turnIRGenDiagnosticsOn,
                      0);
        cli.addOption("--irrun",
                      "Generate and interpret intermediate code",
                      Main::turnIRRunDiagnosticsOn,
                      0);
        cli.addOption("--optir",
                      "Report the intermediate code at the specified phase of optimization.",
                      phase -> reportIR(phase[0]),
                      1);
        cli.addOption("--optcfg",
                "Report the control-flow graph at the specified phase of optimization.",
                phase -> reportCFG(phase[0]),
                1);

        cli.execute(args);

        if(tests) { // put debug mode behaviors here

            String[] exclude = {"enigma"};
            Collections.addAll(Tests.exclude, exclude);
            System.out.println("\nDEBUG: Excluding: " + Tests.exclude.toString());

            try {
               //Tests2.parseTests();
            } catch (Exception ef) {
                ef.printStackTrace();
            }

            //Tests.typeCheckTests();
            //Tests.constantFoldTests();
            //Tests.mirGenTests();
            //Tests.irGenTests();
            //Tests.irRunTests();
            Tests.regressionTest();
        }
    }

    public static void reportIR(String phase) {
        switch(phase) {
            case "initial":
                reportInitialIR = true;
                break;
            case "final":
                reportFinalIR = true;
                break;
            default:
                System.out.println(phase + " is not a supported phase.");
                System.out.println("The IR file will not be written.");
                break;
        }
    }

    public static void reportCFG(String phase) {
        switch(phase) {
            case "initial":
                reportInitialCFG = true;
                break;
            case "final":
                reportFinalCFG = true;
                break;
            default:
                System.out.println(phase + " is not a supported phase.");
                System.out.println("The CFG will not be written.");
                break;
        }
    }

    public static void setSourcePath(String path) {
        sourcePath = path + "/";
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

    public static void setTarget(String os) {
        target = os;
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

    public static void turnOptimizationsOff(String[] args) {
        allOptimizations = false;
    }

    public static boolean debugOn() {
        return debug;
    }

    public static boolean optimizationOn(OptimizationType o) {
        return optimizationMap.get(o);
    }

    public static void turnLexDiagnosticsOn(String[] args) {
        lex = true;
    }

    public static void turnParseDiagnosticsOn(String[] args) {
        parse = true;
    }

    public static void turnTypeCheckDiagnosticsOn(String[] args) {
        typecheck = true;
    }

    public static void turnIRGenDiagnosticsOn(String[] args) {
        irgen = true;
    }

    public static void turnIRRunDiagnosticsOn(String[] args) {
        irrun = true;
    }

    public static boolean lex() {
        return lex;
    }

    public static boolean parse() {
        return parse;
    }

    public static boolean typecheck() {
        return typecheck;
    }

    public static boolean irgen() {
        return irgen;
    }

    public static boolean irrun() {
        return irrun;
    }

    public static boolean reportInitialIR() { return reportInitialIR; }

    public static boolean reportFinalIR() { return reportFinalIR; }

    public static boolean reportInitialCFG() {
        return reportInitialCFG;
    }

    public static boolean reportFinalCFG() {
        return reportFinalCFG;
    }

    public static void turnTestsOn(String[] args) {
        tests = true;
    }

    // these are only used in Tests
    public static void turnParseDiagnosticsOff() {
        parse = false;
    }
    public static void turnTypeCheckDiagnosticsOff() {
        typecheck = false;
    }
    public static void turnIRGenDiagnosticsOff() {
        irgen = false;
    }
    public static void turnIRRunDiagnosticsOff() {
        irrun = false;
    }
}
