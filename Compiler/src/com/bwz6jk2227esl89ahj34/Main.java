package com.bwz6jk2227esl89ahj34;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static Map<Integer, String> symbolTranslation;

    public static void main(String[] args) throws FileNotFoundException{
        symbolTranslation = new HashMap<Integer, String>(){{
            put(0, "if");
            put(1, "while");
            put(2, "else");
            put(3, "return");
            put(4, "length");
            put(5, "int");
            put(6, "bool");
            put(7, "true");
            put(8, "false");
            put(9, "id");
            put(10, "integer");
            put(11, "!");
            put(12, "*");
            put(13, "*>>");
            put(14, "/");
            put(15, "%");
            put(16, "+");
            put(17, "-");
            put(18, "<");
            put(19, "<=");
            put(20, ">=");
            put(21, ">");
            put(22, "==");
            put(23, "!=");
            put(24, "&");
            put(25, "|");
            put(26, "string");
            put(27, "EOF");
            put(28, "(");
            put(29, ")");
            put(30, "[");
            put(31, "]");
            put(32, "{");
            put(33, "}");
            put(34, ".");
            put(35, ":");
            put(36, ",");
            put(37, "="); //TODO: should we change this to eq in sym.java?
            put(38, ";");
            put(39, "character");
            put(40, "use");
            put(404, "error:");
        }};

        CLI cli = new CLI();
        cli.addOption("--lex", "Lex a string", a -> lex(a));
        cli.execute(args);
    }

    public static void lex(String[] args) {
        if(args.length <= 1) {
            System.out.println("please specify input file.");
            return;
        }
        try {
            for (int i = 1; i < args.length; i++) {
                PrintWriter writer = new PrintWriter(args[i].substring(0,
                        args[i].indexOf(".")) + ".lexed");
                FileReader reader = new FileReader(args[i]);
                XiLexer lexer = new XiLexer(reader);
                StringBuilder sb;
                while (true) {
                    java_cup.runtime.Symbol next = lexer.next_token();

                    if (next.sym == 27) {
                        writer.close();
                        break;
                    }
                    sb = new StringBuilder((next.left + 1) + ":" + (next.right +
                            1) + " " + symbolTranslation.get(next.sym));
                    if (next.value != null) {
                        sb.append(" " + next.value);
                    }
                    writer.println(sb.toString());

                    if (next.sym == 404) {
                        writer.close();
                        break;
                    }
                }
                writer.close();
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
    }

}
