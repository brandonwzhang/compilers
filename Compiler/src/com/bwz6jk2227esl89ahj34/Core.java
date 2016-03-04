package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.Program;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
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
                    Util.printError("Lexical", lines.get(lines.size() - 1));
                    break;
                }
                next = lexer.next_token();
            }
        } catch (Exception e) {
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
            return Optional.empty();
        }

        if (parser.hasSyntaxError) {
            // handle syntax error, output to file
            String errMessage = parser.syntaxErrMessage;
            parser.hasSyntaxError = false;
            parser.syntaxErrMessage = "";
            lines.clear();
            lines.add(errMessage);

            Util.printError("Syntax", lines.get(0));

            return Optional.empty();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodeWriterSExpPrinter printer =
                new CodeWriterSExpPrinter(baos);
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

        List<String> parseLines = new ArrayList<>();
        Optional<Symbol> result = parseHelper(parser, parseLines);
        if (!result.isPresent()) {
            // TODO: syntactic error. what do we do?
            // System printing is already taken care of
            return;
        }

        NodeVisitor visitor =
                new TypeCheckVisitor(file.replace(".xi", ""), libPath);

        // attempt typechecking
        try {
            System.out.println(file);
            ((Program) result.get().value).accept(visitor);
            System.out.println("typed");

            // print to file
            ArrayList<String> lines = new ArrayList<String>();
            lines.add("Valid Xi Program");
            Util.writeHelper(file, "typed", diagnosticPath,lines);

        } catch (TypeException e) {
            System.out.println(e.toString());
            //e.printStackTrace();
            //TODO: write diagnostic
        }
    }
}