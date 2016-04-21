package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyPhysicalRegister.Register;
import com.bwz6jk2227esl89ahj34.command_line_interface.CommandLineInterface;

import java.util.Collections;

import com.bwz6jk2227esl89ahj34.assembly.register_allocation.GraphColorer2;

import java.util.*;

public class Main {
    private static String sourcePath = "./";
    private static String diagnosticPath = "./";
    private static String libPath = "./";
    private static String assemblyPath = "./";
    private static String target = "linux";
    private static boolean debug;
    private static boolean optimizations = true;
    private static boolean tests;
    private static boolean lex;
    private static boolean parse;
    private static boolean typecheck;
    private static boolean irgen;
    private static boolean irrun;

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
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

        //cli.execute(args);

        // test for first GraphColorer
//        Node a = new Node();
//        Node b = new Node();
//        Node c = new Node();
//
//        a.addNeighbor(b);
//        b.addNeighbor(a);
//
//        b.addNeighbor(c);
//        c.addNeighbor(b);
//
//        a.addNeighbor(c);
//        c.addNeighbor(a);
//
//        List<Node> graph = new ArrayList<>();
//        graph.add(a);
//        graph.add(b);
//        graph.add(c);
//
//        System.out.println("coloring");
//        GraphColorer gc = new GraphColorer(graph);
//        boolean colored = gc.colorGraph();
//        System.out.println(colored);
//        System.out.println(a.color);
//        System.out.println(b.color);
//        System.out.println(c.color);

        // test for GraphColorer2
//        AssemblyAbstractRegister a = new AssemblyAbstractRegister();
//        AssemblyAbstractRegister b = new AssemblyAbstractRegister();
//        AssemblyAbstractRegister c = new AssemblyAbstractRegister();
//
//        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
//        List<AssemblyAbstractRegister> an = new ArrayList<>();
//        an.add(b);
//        List<AssemblyAbstractRegister> bn = new ArrayList<>();
//        bn.add(a);
//        bn.add(c);
//        List<AssemblyAbstractRegister> cn = new ArrayList<>();
//        cn.add(b);
//        graph.put(a, an);
//        graph.put(b, bn);
//        graph.put(c, cn);

        // test for GraphColorer2
//        AssemblyAbstractRegister a = new AssemblyAbstractRegister();
//        AssemblyAbstractRegister b = new AssemblyAbstractRegister();
//        AssemblyAbstractRegister c = new AssemblyAbstractRegister();
//        AssemblyAbstractRegister d = new AssemblyAbstractRegister();
//        AssemblyAbstractRegister e = new AssemblyAbstractRegister();
//
//        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
//        List<AssemblyAbstractRegister> an = new ArrayList<>(); an.add(b); an.add(c); an.add(d); an.add(e);
//        List<AssemblyAbstractRegister> bn = new ArrayList<>(); bn.add(a); bn.add(c); bn.add(d); bn.add(e);
//        List<AssemblyAbstractRegister> cn = new ArrayList<>(); cn.add(a); cn.add(b); cn.add(d); cn.add(e);
//        List<AssemblyAbstractRegister> dn = new ArrayList<>(); dn.add(a); dn.add(b); dn.add(c); dn.add(e);
//        List<AssemblyAbstractRegister> en = new ArrayList<>(); en.add(a); en.add(b); en.add(c); en.add(d);
//        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn); graph.put(e, en);
//
//        System.out.println("coloring");
//        GraphColorer2 gc = new GraphColorer2(graph);
//        //gc.addColoring(a, AssemblyPhysicalRegister.Register.RBX);
//        boolean colored = gc.colorGraph();
//        Map<AssemblyAbstractRegister, Register> coloring = gc.getColoring();
//
//        System.out.println(colored);
//        System.out.println(coloring.get(a));
//        System.out.println(coloring.get(b));
//        System.out.println(coloring.get(c));
//        System.out.println(coloring.get(d));
//        System.out.println(coloring.get(e));

        if(tests) { // put debug mode behaviors here

            String[] exclude = {"enigma", "medley01", "array_init"};
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

    /**
     * Turns on optimizations (constant folding).
     */
    public static void turnOptimizationsOff(String[] args) {
        optimizations = false;
    }

    public static boolean debugOn() {
        return debug;
    }

    public static boolean optimizationsOn() {
        return optimizations;
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
