package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.FunctionDeclaration;
import com.bwz6jk2227esl89ahj34.AST.Program;
import com.bwz6jk2227esl89ahj34.AST.UseStatement;
import com.bwz6jk2227esl89ahj34.AST.parse.Lexer;
import com.bwz6jk2227esl89ahj34.AST.parse.Parser;
import com.bwz6jk2227esl89ahj34.AST.parse.ParserSym;
import com.bwz6jk2227esl89ahj34.AST.type.TypeException;
import com.bwz6jk2227esl89ahj34.AST.visit.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyProgram;
import com.bwz6jk2227esl89ahj34.ir.IRCompUnit;
import com.bwz6jk2227esl89ahj34.ir.interpret.IRSimulator;
import com.bwz6jk2227esl89ahj34.ir.parse.IRLexer;
import com.bwz6jk2227esl89ahj34.ir.parse.IRParser;
import com.bwz6jk2227esl89ahj34.ir.visit.CheckCanonicalIRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRConstantFoldingVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.MIRLowerVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.CodeWriterSExpPrinter;
import com.bwz6jk2227esl89ahj34.util.Util;
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
     * Writes the output to diagnosticPath + file.
     */
    public static void lexFile(String sourcePath,
                               String diagnosticPath,
                               String file) {

        Optional<FileReader> reader = Util.getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());

        List<String> lines = new ArrayList<>();
        lexHelper(lexer, lines);
        Util.writeHelper(file, "lexed", diagnosticPath, lines);
    }

    /**
     * A helper function for lexing. Lexes with the given lexer.
     * Mutates the given list by adding the lines of the output.
     *
     * @param lexer a Lexer object
     * @param lines a List<String> for storing the output
     */
    public static void lexHelper(Lexer lexer, List<String> lines) {
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
            // something is wrong with the lexer
            e.printStackTrace();
        }
    }

    /**
     * Parses the Xi file located at sourcePath + file.
     * Writes the output to diagnosticPath + file.
     * The output is an S-expression representing the AST of the given program.
     */
    public static void parseFile(String sourcePath,
                                 String diagnosticPath,
                                 String file) {

        Optional<FileReader> reader = Util.getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);

        List<String> lines = new ArrayList<>();
        parseHelper(parser, lines);
        Util.writeHelper(file, "parsed", diagnosticPath, lines);
    }

    /**
     * A helper function for parsing. Parses with the given parser.
     * Mutates the given list by adding the lines of the output.
     * If there is a syntax error, the given list is cleared and a
     * single String containing the parser's error message is added.
     *
     * If an exception is thrown during parsing or if there is a syntax
     * error, an empty Optional is returned. If the parsing is successful,
     * an Optional containing the result is returned.
     *
     * @param parser a Parser object
     * @param lines a List<String> for storing the output
     * @return an Optional that might contain the result of the parsing
     */
    public static Optional<Symbol> parseHelper(Parser parser,
                                               List<String> lines) {
        Symbol result;
        try {
            result = parser.parse();
        } catch (Exception e) {
            e.printStackTrace();
            // something is wrong with the parser
            return Optional.empty();
        }

        if (parser.hasSyntaxError) {
            // there was a syntax error
            // update lines and return no result
            String errMessage = parser.syntaxErrMessage;
            parser.hasSyntaxError = false;
            parser.syntaxErrMessage = "";
            lines.clear();
            lines.add(errMessage);

            Util.printError("Syntax", lines.get(0));

            return Optional.empty();
        }

        // print the AST, update lines, and return the result
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(baos);
        NodeVisitor visitor = new PrintVisitor(printer);

        ((Program)(result.value)).accept(visitor);

        printer.flush();
        lines.add(baos.toString());

        return Optional.of(result);
    }

    /**
     * Typechecks the Xi file located at sourcePath + file.
     * Writes the output to diagnosticPath + file.
     *
     * libPath is the path where the interface files (.ixi)
     * are found.
     */
    public static void typeCheck(String sourcePath,
                                 String diagnosticPath,
                                 String libPath,
                                 String file) {
        Optional<FileReader> reader = Util.getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);
        List<String> lines = new ArrayList<>();
        Optional<Program> program = typeCheckHelper(parser, libPath, lines);
        Util.writeHelper(file, "typed", diagnosticPath, lines);

        if (program.isPresent()) {
            if (Main.debugOn()) {
                System.out.println("DEBUG: typed");
            }
        }
    }

    /**
     * A helper function for typechecking.
     *
     * Parses with the given parser. If there is a syntax error,
     * an error message will be stored in the given list.
     *
     * If the program types, then it will be returned inside an Optional.
     * If there is a semantic error, an empty Optional will be returned
     * and an error message will be stored in the given list.
     *
     * @param parser a Parser object
     * @param lines a List<String> for storing the output. Any strings that
     *              are in here before the method call will be lost.
     * @return an Optional that might contain the result of the parsing
     */
    public static Optional<Program> typeCheckHelper(Parser parser,
                                                    String libPath,
                                                    List<String> lines) {
        lines.clear();

        Optional<Symbol> result = parseHelper(parser, lines);
        if (!result.isPresent()) {
            return Optional.empty();
        }
        lines.clear();

        Program program = (Program) result.get().value;
        NodeVisitor visitor = new TypeCheckVisitor(libPath);
        try {
            program.accept(visitor);
            lines.add("Valid Xi Program");
            return Optional.of(program);
        } catch (TypeException e) {
            Util.printError("Semantic", e.toString());
            lines.add(e.toString());
            return Optional.empty();
        }
    }

    /**
     * Reads in a Xi source file, typechecks it, and translates it
     * to an MIR (IR that has not been lowered).
     *
     * If the translation succeeds, the root of the MIR tree will be
     * returned inside an Optional. If an error occurs (e.g. semantic
     * error, syntax error), then an empty Optional is returned.
     */
    public static Optional<IRCompUnit> translateToMIR(
            String sourcePath,
            String libPath,
            String file) {

        Optional<FileReader> reader = Util.getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return Optional.empty();
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);
        List<String> lines = new ArrayList<>();
        Optional<Program> program = typeCheckHelper(parser, libPath, lines);
        if (!program.isPresent()) {
            return Optional.empty();
        }

        if (Main.optimizationsOn()) {
            // constant folding on the AST
            NodeVisitor cfv = new ConstantFoldingVisitor();
            program.get().accept(cfv);
            // annotate the new nodes with types
            NodeVisitor tcv = new TypeCheckVisitor(libPath);
            program.get().accept(tcv);
        }


        MIRGenerateVisitor mirgv =
                new MIRGenerateVisitor(Util.extractFileName(file));
        program.get().accept(mirgv);
        IRCompUnit mirRoot = mirgv.getRoot();

        if (Main.optimizationsOn()) {
            // constant folding on the MIR tree
            IRVisitor mircfv = new IRConstantFoldingVisitor();
            mirRoot = (IRCompUnit) mircfv.visit(mirRoot);
        }

        return Optional.of(mirRoot);
    }

    /**
     * Intermediate method for ease of testing.
     *
     * Generates the MIR of the given Xi source file.
     * If debug mode is one, the S-Expression representation will
     * be written to a .mir file.
     */
    public static Optional<IRCompUnit> mirGen(String sourcePath,
                             String diagnosticPath,
                             String libPath,
                             String file) {
        Optional<IRCompUnit> root =
                translateToMIR(sourcePath, libPath, file);
        if (!root.isPresent()) {
            return Optional.empty();
        }

        if (Main.debugOn()) {
            Util.writeIRTree(root.get(), diagnosticPath, file, "mir");
        }

        return Optional.of(root.get());
    }

    /**
     * Generates and writes the IR of the given Xi source file.
     */
    public static Optional<IRCompUnit> irGen(
            String sourcePath,
            String diagnosticPath,
            String libPath,
            String file,
            boolean write) {
        Optional<IRCompUnit> mirRoot =
                mirGen(sourcePath, diagnosticPath, libPath, file);
        if (!mirRoot.isPresent()) {
            return Optional.empty();
        }

        MIRLowerVisitor mirv = new MIRLowerVisitor();
        IRCompUnit lirRoot = (IRCompUnit) mirv.visit(mirRoot.get());

        if (Main.optimizationsOn()) {
            IRVisitor mircfv = new IRConstantFoldingVisitor();
            lirRoot = (IRCompUnit) mircfv.visit(lirRoot);
        }

        if (write) {
            Util.writeIRTree(lirRoot, diagnosticPath, file, "ir");
        }

        return Optional.of(lirRoot);
    }

    /**
     * Generates and writes the IR of the given Xi source file.
     * Runs the Xi program by interpreting the IR.
     */
    public static void irRun(String sourcePath,
                             String diagnosticPath,
                             String libPath,
                             String file) {
        // TODO: lower the IR (use irGen instead of mirGen)

        // reads Xi source file and writes an .mir file
        irGen(sourcePath, diagnosticPath, libPath, file, true);

        Optional<FileReader> reader =
                Util.getFileReader(
                        diagnosticPath,
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

        CheckCanonicalIRVisitor cv = new CheckCanonicalIRVisitor();
        System.out.println(cv.visit(root));
        //System.out.println(root);

        System.out.println("====================================");
        IRSimulator sim = new IRSimulator(root);
        sim.call("_Imain_paai", 0);
    }

    public static void generateAssembly(String sourcePath,
                                        String diagnosticPath,
                                        String libPath,
                                        String assemblyPath,
                                        String file) {
        Optional<FileReader> reader = Util.getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);
        List<String> lines = new LinkedList<>();
        Optional<Program> programASTOptional = typeCheckHelper(parser, libPath, lines);

        List<String> IRfunctionNamesFromUseStatements = new LinkedList<>();
        if (programASTOptional.isPresent()) {
            Program programAST = programASTOptional.get();
            List<FunctionDeclaration> useFunctionDeclarations =
                    programAST.getFunctionsFromUseStatement(libPath);
            for(FunctionDeclaration useFunctionDeclaration :
                    useFunctionDeclarations) {
                IRfunctionNamesFromUseStatements.add(Util.getIRFunctionName(useFunctionDeclaration));
            }
        }

        Optional<IRCompUnit> irRoot = irGen(sourcePath, diagnosticPath, libPath, file, true);
        if (!irRoot.isPresent()) {
            return;
        }


        AssemblyProgram program = new AssemblyProgram(irRoot.get(),
                IRfunctionNamesFromUseStatements);
        Util.writeHelper(file, "s", assemblyPath, Collections.singletonList(program.toString()));

        // System.out.println("Assembly code generation has not been implemented yet.");
    }
}
