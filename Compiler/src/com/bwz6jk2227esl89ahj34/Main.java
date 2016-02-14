package com.bwz6jk2227esl89ahj34;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static Map<Integer, String> symbolTranslation;

    public static void main(String[] args) throws FileNotFoundException{
        /*
            HashMap used to map sym constants to printable tokens.
         */
        symbolTranslation = new HashMap<Integer, String>(){{
            put(sym.IF, "if");
            put(sym.WHILE, "while");
            put(sym.ELSE, "else");
            put(sym.RETURN, "return");
            put(sym.LENGTH, "length");
            put(sym.INT, "int");
            put(sym.BOOL, "bool");
            put(sym.TRUE, "true");
            put(sym.FALSE, "false");
            put(sym.IDENTIFIER, "id");
            put(sym.INTEGER_LITERAL, "integer");
            put(sym.LOGICAL_NEG, "!");
            put(sym.TIMES, "*");
            put(sym.HIGH_MULT, "*>>");
            put(sym.DIVIDE, "/");
            put(sym.MOD, "%");
            put(sym.PLUS, "+");
            put(sym.MINUS, "-");
            put(sym.LESS, "<");
            put(sym.LESS_EQ, "<=");
            put(sym.GREATER_EQ, ">=");
            put(sym.GREATER, ">");
            put(sym.EQEQ, "==");
            put(sym.NOT_EQ, "!=");
            put(sym.AND, "&");
            put(sym.OR, "|");
            put(sym.STRING_LITERAL, "string");
            put(sym.EOF, "EOF");
            put(sym.LEFT_PAREN, "(");
            put(sym.RIGHT_PAREN, ")");
            put(sym.LEFT_SQUARE_BRACKET, "[");
            put(sym.RIGHT_SQUARE_BRACKET, "]");
            put(sym.LEFT_CURLY_BRACKET, "{");
            put(sym.RIGHT_CURLY_BRACKET, "}");
            put(sym.PERIOD, ".");
            put(sym.COLON, ":");
            put(sym.COMMA, ",");
            put(sym.ASSIGNMENT, "="); //TODO: should we change this to eq in sym.java?
            put(sym.SEMICOLON, ";");
            put(sym.CHAR_LITERAL, "character");
            put(sym.USE, "use");
            put(sym.UNDERSCORE, "_");
            put(sym.ERROR, "error:");
        }};

        CLI cli = new CLI();
        cli.addOption("--lex", "Lex a file", Main::lex);
        cli.addOption("--parse", "Parse a .xi file to a .parsed file",
                Main::parse);
        // TODO: -sourcepath, -D
        cli.execute(args);
    }

    public static void lex(String[] args) {
        if(args.length == 0) {
            System.out.println("Please specify input file.");
            return;
        }
        try {
            for (int i = 0; i < args.length; i++) {
                ArrayList<String> lines = new ArrayList<>();
                PrintWriter writer = new PrintWriter(args[i].substring(0,
                        args[i].indexOf(".")) + ".lexed");
                FileReader reader = new FileReader(args[i]);
                XiLexer lexer = new XiLexer(reader);
                StringBuilder sb;
                while (true) {
                    java_cup.runtime.Symbol next = lexer.next_token();
                    if (next.sym == sym.EOF) {
                        writeAndClose(writer, lines);
                        break;
                    }
                    sb = new StringBuilder((next.left + 1) + ":" +
                            (next.right + 1) + " " +
                            symbolTranslation.get(next.sym));
                    if (next.value != null) {
                        sb.append(" " + next.value);
                    }
                    lines.add(sb.toString());

                    if (next.sym == sym.ERROR) {
                        writeAndClose(writer, lines);
                        break;
                    }
                }
                writer.close();
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
    }

    public static void parse(String[] args) {
        // TODO:
    }

    public static void writeAndClose(PrintWriter writer, ArrayList<String> lines) {
        int i = 0;
        while ( i < lines.size() - 1) {
            writer.println(lines.get(i));
            i++;
        }
        writer.print(lines.get(i));
        writer.close();
    }

}
