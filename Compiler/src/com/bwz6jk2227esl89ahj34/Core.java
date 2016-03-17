package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.Program;
import com.bwz6jk2227esl89ahj34.ir.IRCompUnit;
import com.bwz6jk2227esl89ahj34.ir.interpret.IRSimulator;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.MIRVisitor;
import com.bwz6jk2227esl89ahj34.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
     * libPath is the path where the interface files (.ixi)
     * are found.
     *
     * Returns the typechecked program if it types.
     */
    public static Optional<Program> typeCheck(String sourcePath,
                                 String diagnosticPath,
                                 String libPath,
                                 String file) {
        Optional<FileReader> reader = Util.getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return Optional.empty();
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);

        // parse the file
        List<String> parseLines = new ArrayList<>();
        Optional<Symbol> result = parseHelper(parser, parseLines);
        if (!result.isPresent()) {
            // printing to standard output is already taken care of
            Util.writeHelper(file, "typed", diagnosticPath, parseLines);
            return Optional.empty();
        }

        Program program = (Program) result.get().value;
        Optional<String> typeCheckErrorMessage = typeCheckHelper(program, libPath);

        if (!typeCheckErrorMessage.isPresent()) {
            if (Main.debugOn()) {
                System.out.println("DEBUG: typed");
            }

            List<String> lines = Collections.singletonList("Valid Xi Program");
            Util.writeHelper(file, "typed", diagnosticPath, lines);

            return Optional.of(program);
        } else {
            Util.printError("Semantic", typeCheckErrorMessage.get());
            List<String> lines = Collections.singletonList(typeCheckErrorMessage.get());
            Util.writeHelper(file, "typed", diagnosticPath, lines);
            return Optional.empty();
        }
    }

    /**
     * Typechecks the given program. Returns an error message if a type
     * error occurs.
     *
     * @param node a program
     * @param libPath the path that contains Xi interface files
     * @return the error message wrapped in an Optional
     */
    public static Optional<String> typeCheckHelper(Program node, String libPath) {
        NodeVisitor visitor =
                new TypeCheckVisitor(libPath);
        try {
            node.accept(visitor);
            return Optional.empty();
        } catch (TypeException e) {
            String errorMessage = e.toString();
            return Optional.of(errorMessage);
        }
    }

    public static Optional<IRCompUnit> mirGen(String sourcePath,
                             String diagnosticPath,
                             String libPath,
                             String file) {
        Optional<Program> root = typeCheck(sourcePath, diagnosticPath, libPath, file);
        if (!root.isPresent()) {
            return Optional.empty();
        }

        MIRGenerateVisitor visitor = new MIRGenerateVisitor("");
        root.get().accept(visitor);

        printIRTree(visitor.getIRRoot(), diagnosticPath, file, "mir");

        return Optional.of(visitor.getIRRoot());
    }

    public static void irGen(String sourcePath,
                             String diagnosticPath,
                             String libPath,
                             String file) {
        Optional<IRCompUnit> mirRoot = mirGen(sourcePath, diagnosticPath, libPath, file);

        if (!mirRoot.isPresent()) {
            return;
        }

        MIRVisitor mirVisitor = new MIRVisitor();
        IRCompUnit lirRoot = (IRCompUnit) mirVisitor.visit(mirRoot.get());

        printIRTree(lirRoot, diagnosticPath, file, "ir");
    }

    public static void printIRTree(IRCompUnit root,
                                   String diagnosticPath,
                                   String file,
                                   String extension) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(baos);
        root.printSExp(printer);
        printer.flush();
        List<String> lines = Collections.singletonList(baos.toString());
        Util.writeHelper(file, extension, diagnosticPath, lines);
    }

    public static void irRun(String sourcePath,
                             String diagnosticPath,
                             String libPath,
                             String file) {

        Optional<Program> root = typeCheck(sourcePath, diagnosticPath, libPath, file);
        if (!root.isPresent()) {
            return;
        }

        MIRGenerateVisitor visitor = new MIRGenerateVisitor("");
        root.get().accept(visitor);
        IRSimulator sim = new IRSimulator(visitor.getIRRoot());
        long callResult = sim.call("_Imain_p", 0);
        System.out.println(callResult);
    }
}
