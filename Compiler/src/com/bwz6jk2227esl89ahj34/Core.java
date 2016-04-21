package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyFunction;
import com.bwz6jk2227esl89ahj34.ast.FunctionDeclaration;
import com.bwz6jk2227esl89ahj34.ast.Program;
import com.bwz6jk2227esl89ahj34.ast.parse.Lexer;
import com.bwz6jk2227esl89ahj34.ast.parse.Parser;
import com.bwz6jk2227esl89ahj34.ast.parse.ParserSym;
import com.bwz6jk2227esl89ahj34.ast.type.TypeException;
import com.bwz6jk2227esl89ahj34.ast.visit.*;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyProgram;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGIR;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.live_variables
        .LiveVariableAnalysis;
import com.bwz6jk2227esl89ahj34.ir.IRCompUnit;
import com.bwz6jk2227esl89ahj34.ir.IRSeq;
import com.bwz6jk2227esl89ahj34.ir.interpret.IRSimulator;
import com.bwz6jk2227esl89ahj34.ir.parse.IRLexer;
import com.bwz6jk2227esl89ahj34.ir.parse.IRParser;
import com.bwz6jk2227esl89ahj34.ir.visit.CheckCanonicalIRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRConstantFoldingVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.MIRLowerVisitor;
import com.bwz6jk2227esl89ahj34.util.Util;
import com.bwz6jk2227esl89ahj34.util.prettyprint.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.util.*;

/**
 * A class containing the methods that make up the core functionality
 * of this compiler.
 */
public class Core {
    /**
     * Lexes the Xi file located at sourcePath + file.
     * Diagnostics are written to diagnosticPath + file.
     */
    public static void lexFile(String file) {

        Optional<FileReader> reader = Util.getFileReader(Main.sourcePath(), file);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());
        List<String> lines = new ArrayList<>();

        try {
            Symbol next = lexer.next_token();
            while (next.sym != ParserSym.EOF) {
                String line = next.left + ":" +
                        next.right + " " +
                        Util.symbolTranslation.get(next.sym);
                if (next.value != null) {
                    line += " " + next.value;
                }
                lines.add(line);
                if (next.sym == ParserSym.error) {
                    // lex error occurred
                    Util.printError("Lexical", lines.get(lines.size() - 1));
                    break;
                }
                next = lexer.next_token();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something is wrong with the lexer. " +
                    "Not necessarily a lex error in the source file.");
        }

        if (Main.lex()) {
            Util.writeHelper(file, "lexed", Main.diagnosticPath(), lines);
        }
    }

    /**
     * Parses the Xi file located at sourcePath + file.
     * Writes the output to diagnosticPath + file.
     * The output is an S-expression representing the AST of the given program.
     */
    public static Optional<Program> parseFile(String file) {

        Optional<FileReader> reader = Util.getFileReader(Main.sourcePath(), file);
        if (!reader.isPresent()) {
            return Optional.empty();
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);
        List<String> lines = new ArrayList<>();

        Symbol result;
        try {
            result = parser.parse();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something is wrong with the parser. " +
                    "Or maybe there's a lex error.");
            return Optional.empty();
        }

        if (parser.hasSyntaxError) {
            // there was a syntax error
            // update lines and return no result
            String errMessage = parser.syntaxErrMessage;
            parser.hasSyntaxError = false;
            parser.syntaxErrMessage = "";
            lines.add(errMessage);

            Util.printError("Syntax", lines.get(0));

            return Optional.empty();
        }

        Program program = (Program) result.value;

        if (Main.parse()) {
            // print the AST
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(baos);
            NodeVisitor visitor = new PrintVisitor(printer);
            program.accept(visitor);

            printer.flush();
            lines.clear();
            lines.add(baos.toString());
            Util.writeHelper(file, "parsed", Main.diagnosticPath(), lines);
        }

        return Optional.of(program);
    }

    /**
     * Typechecks the Xi file located at sourcePath + file.
     * Writes the output to diagnosticPath + file.
     *
     * libPath is the path where the interface files (.ixi)
     * are found.
     */
    public static Optional<Program> typeCheck(String file, Program program) {

        List<String> lines = new ArrayList<>();
        NodeVisitor visitor = new TypeCheckVisitor(Main.libPath());
        boolean typed = false;
        try {
            program.accept(visitor);
            lines.add("Valid Xi Program");
            if (Main.debugOn()) {
                System.out.println("DEBUG: typed");
            }
            typed = true;
        } catch (TypeException e) {
            Util.printError("Semantic", e.toString());
            lines.add(e.toString());
        }

        if (Main.typecheck()) {
            Util.writeHelper(file, "typed", Main.diagnosticPath(), lines);
        }

        if (typed) {
            return Optional.of(program);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Intermediate method for ease of testing.
     *
     * Generates the MIR of the given Xi source file.
     * If debug mode is one, the S-Expression representation will
     * be written to a .mir file.
     */
    public static IRCompUnit mirGen(String file, Program program) {

        if (Main.optimizationsOn()) {
            // constant folding on the AST
            NodeVisitor cfv = new ConstantFoldingVisitor();
            program.accept(cfv);
            // annotate the new nodes with types
            NodeVisitor tcv = new TypeCheckVisitor(Main.libPath());
            program.accept(tcv);
        }

        MIRGenerateVisitor mirgv =
                new MIRGenerateVisitor(Util.extractFileName(file));
        program.accept(mirgv);
        IRCompUnit mirRoot = mirgv.getRoot();

        if (Main.optimizationsOn()) {
            // constant folding on the MIR tree
            IRVisitor mircfv = new IRConstantFoldingVisitor();
            mirRoot = (IRCompUnit) mircfv.visit(mirRoot);
        }

        if (Main.irgen() && Main.debugOn()) {
            Util.writeIRTree(mirRoot, Main.diagnosticPath(), file, "mir");
        }

        return mirRoot;
    }

    /**
     * Generates and writes the IR of the given Xi source file.
     */
    public static IRCompUnit irGen(String file, IRCompUnit mirRoot) {

        MIRLowerVisitor mirv = new MIRLowerVisitor();
        IRCompUnit lirRoot = (IRCompUnit) mirv.visit(mirRoot);

        // check that the IR is canonical
        CheckCanonicalIRVisitor cv = new CheckCanonicalIRVisitor();
        assert cv.visit(lirRoot);

        if (Main.optimizationsOn()) {
            IRVisitor mircfv = new IRConstantFoldingVisitor();
            lirRoot = (IRCompUnit) mircfv.visit(lirRoot);
        }

        if (Main.irgen() || Main.irrun()) {
            Util.writeIRTree(lirRoot, Main.diagnosticPath(), file, "ir");
        }

        return lirRoot;
    }

    /**
     * Runs the Xi program by interpreting the IR.
     */
    public static void irRun(String file) {
        // TODO: lower the IR (use irGen instead of mirGen)

        Optional<FileReader> reader =
                Util.getFileReader(
                        Main.diagnosticPath(),
                        file.substring(0, file.length() - 2) + "ir"
                );
        if (!reader.isPresent()) {
            return;
        }

        IRLexer lexer = new IRLexer(reader.get());
        IRParser parser = new IRParser(lexer);
        Symbol result;
        try {
            result = parser.parse();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("IR parsing failed");
        }

        IRCompUnit root = result.value();
        IRSimulator sim = new IRSimulator(root);
        sim.call("_Imain_paai", 0);
    }

    public static void generateAssembly(String file) {

        lexFile(file);

        Optional<Program> program = parseFile(file);
        if (!program.isPresent()) {
            return;
        }

        program = typeCheck(file, program.get());
        if (!program.isPresent()) {
            return;
        }

        IRCompUnit mirRoot = mirGen(file, program.get());
        IRCompUnit lirRoot = irGen(file, mirRoot);

        // Testing IR CFG
        for (String functionName : lirRoot.functions().keySet()) {
            CFGIR graph =
                    new CFGIR((IRSeq)lirRoot.functions().get(functionName).body());
            Util.writeHelper(
                    "IR_dotfile_" + functionName,
                    "dot",
                    "./",
                    Collections.singletonList(graph.toString())
            );
        }

        if (Main.irrun()) {
            irRun(file);
        }

        // Get the names of functions defined in files that are imported.
        List<String> IRfunctionNamesFromUseStatements = new LinkedList<>();
        if (program.isPresent()) {
            List<FunctionDeclaration> useFunctionDeclarations =
                    program.get().getFunctionsFromUseStatement(Main.libPath());
            for(FunctionDeclaration useFunctionDeclaration : useFunctionDeclarations) {
                IRfunctionNamesFromUseStatements.add(Util.getIRFunctionName(useFunctionDeclaration));
            }
        }

        // Instantiate an AssemblyProgram, which processes the ir and prepares
        // the assembly code.
        AssemblyProgram assemblyProgram =
                new AssemblyProgram(
                        lirRoot,
                        IRfunctionNamesFromUseStatements,
                        Main.target()
                );
        Util.writeHelper(
                file,
                "s",
                Main.assemblyPath(),
                Collections.singletonList(assemblyProgram.toString())
        );


        // Link and run the assembly file
        String fileName = file.substring(0, file.lastIndexOf('.'));
        ProcessBuilder pb =
                new ProcessBuilder(Util.rootPath + "/runtime/linkxi.sh", "-o", fileName, Main.assemblyPath() + "/" + fileName + ".s").inheritIO();
        try {
            Process linkProcess = pb.start();
            linkProcess.waitFor();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
