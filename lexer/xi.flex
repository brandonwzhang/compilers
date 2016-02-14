package com.bwz6jk2227esl89ahj34;
import java_cup.runtime.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.PrintWriter;

%%
%class XiLexer
%unicode
%cup
%line
%column

%{
   public static final HashMap<Integer, String> symbolTranslation = new HashMap<Integer, String>(){{
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
     put(sym.NOT, "!");
     put(sym.TIMES, "*");
     put(sym.HIGH_MULT, "*>>");
     put(sym.DIVIDE, "/");
     put(sym.MODULO, "%");
     put(sym.PLUS, "+");
     put(sym.MINUS, "-");
     put(sym.LT, "<");
     put(sym.LEQ, "<=");
     put(sym.GEQ, ">=");
     put(sym.GT, ">");
     put(sym.EQUAL, "==");
     put(sym.NOT_EQUAL, "!=");
     put(sym.AND, "&");
     put(sym.OR, "|");
     put(sym.STRING_LITERAL, "string");
     put(sym.EOF, "EOF");
     put(sym.OPEN_PAREN, "(");
     put(sym.CLOSE_PAREN, ")");
     put(sym.OPEN_BRACKET, "[");
     put(sym.CLOSE_BRACKET, "]");
     put(sym.OPEN_BRACE, "{");
     put(sym.CLOSE_BRACE, "}");
     put(sym.PERIOD, ".");
     put(sym.COLON, ":");
     put(sym.COMMA, ",");
     put(sym.ASSIGNMENT, "="); //TODO: should we change this to eq in sym.java?
     put(sym.SEMICOLON, ";");
     put(sym.CHARACTER_LITERAL, "character");
     put(sym.USE, "use");
     put(sym.UNDERSCORE, "_");
     put(sym.error, "error:");
   }};

   public static void lexFile(String sourcePath, String diagnosticPath, String arg) {
     String[] files = arg.split(":");
     for (int i = 0; i < files.length; i++) {
       files[i] = sourcePath + files[i];
     }
     if (files.length == 0) {
       System.out.println("Please specify input file.");
       return;
     }
     try {
       for (int i = 0; i < files.length; i++) {
         ArrayList<String> lines = new ArrayList<String>();
         FileReader reader = new FileReader(files[i]);
         XiLexer lexer = new XiLexer(reader);
         Symbol next = lexer.next_token();
         while (next.sym != sym.EOF && next.sym != sym.error) {
           String line = (next.left + 1) + ":" +
                         (next.right + 1) + " " +
                         symbolTranslation.get(next.sym);
           if (next.value != null) {
             line += " " + next.value;
           }
           lines.add(line);
         }
         String writeFile = diagnosticPath + 
                            files[i].substring(0, files[i].indexOf(".")) + ".lexed";
         Main.writeAndClose(new PrintWriter(writeFile), lines);
       }
     } catch(Exception e) {
       e.printStackTrace();
     }
   }

   StringBuffer string = new StringBuffer();
   int stringStartCol = -1;
   int stringStartRow = -1;
   StringBuffer hexBuffer = new StringBuffer();

   private Symbol symbol(int type) {
     return new Symbol(type, yyline, yycolumn);
   }
   private Symbol symbol(int type, Object value) {
     return new Symbol(type, yyline, yycolumn, value);
   }

   /* Converts a char containing hex (eg. '\x64') to a string (eg. "d") */
   private String hexToString(String hex) {
     hexBuffer.setLength(0);
     String str = hex.substring(2, 4);
     hexBuffer.append((char)Integer.parseInt(str, 16));
     return hexBuffer.toString();
   }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

Comment = "//" {InputCharacter}* {LineTerminator}?
Letter = [A-Za-z]
Identifier = {Letter} ({Letter} | [0-9_'])*
DecIntegerLiteral = 0 | [1-9][0-9]*

HexChar = \\x[2-7][0-9A-E]

%state STRING
%%

<YYINITIAL> {
 /* keywords */
"use"                               { return symbol(sym.USE); }
"if"                                { return symbol(sym.IF); }
"while"                             { return symbol(sym.WHILE); }
"else"                              { return symbol(sym.ELSE); }
"return"                            { return symbol(sym.RETURN); }
"length"                            { return symbol(sym.LENGTH); }
"int"                               { return symbol(sym.INT); }
"bool"                              { return symbol(sym.BOOL); }
"true"                              { return symbol(sym.TRUE); }
"false"                             { return symbol(sym.FALSE); }

 {Identifier}                       { return symbol(sym.IDENTIFIER, yytext()); }

 /* literals */
 {DecIntegerLiteral}                { if(yytext().length() > "9223372036854775808".length() || yytext().compareTo("9223372036854775808") > 0) { return symbol(sym.error, "Integer literal is too big to process"); } else {return symbol(sym.INTEGER_LITERAL, yytext()); } }
 \"                                 { string.setLength(0); stringStartRow = yyline; stringStartCol = yycolumn; yybegin(STRING); }

 /* char literals */
 \'[^\n\r]\'                        { return symbol(sym.CHARACTER_LITERAL, yytext().charAt(1)); }
 "'\n'"                             { return symbol(sym.CHARACTER_LITERAL, yytext().charAt(1)); }
 "'\r'"                             { return symbol(sym.CHARACTER_LITERAL, yytext().charAt(1)); }
 \'{HexChar}\'                      { return symbol(sym.CHARACTER_LITERAL, hexToString(yytext().substring(1, yytext().length()-1))); }

 \'[^\']*\'                           { return symbol(sym.error, "Invalid character constant"); }

 /* terminals */
 "("                                { return symbol(sym.OPEN_PAREN); }
 ")"                                { return symbol(sym.CLOSE_PAREN); }
 "["                                { return symbol(sym.OPEN_BRACKET); }
 "]"                                { return symbol(sym.CLOSE_BRACKET); }
 "{"                                { return symbol(sym.OPEN_BRACE); }
 "}"                                { return symbol(sym.CLOSE_BRACE); }
 "."                                { return symbol(sym.PERIOD); }
 ":"                                { return symbol(sym.COLON); }
 ";"                                { return symbol(sym.SEMICOLON); }
 ","                                { return symbol(sym.COMMA); }
 "_"                                { return symbol(sym.UNDERSCORE); }

 /* operators ordered by precedence */
 "!"      { return symbol(sym.NOT); }
 "*"      { return symbol(sym.TIMES); }
 "*>>"    { return symbol(sym.HIGH_MULT); }
 "/"      { return symbol(sym.DIVIDE); }
 "%"      { return symbol(sym.MODULO); }
 "+"      { return symbol(sym.PLUS); }
 "-"      { return symbol(sym.MINUS); }
 "<"      { return symbol(sym.LT); }
 "<="     { return symbol(sym.LEQ); }
 ">="     { return symbol(sym.GEQ); }
 ">"      { return symbol(sym.GT); }
 "=="     { return symbol(sym.EQUAL); }
 "!="     { return symbol(sym.NOT_EQUAL); }
 "&"      { return symbol(sym.AND); }
 "|"      { return symbol(sym.OR); }
 "="        { return symbol(sym.ASSIGNMENT); }


    /* comments */
    {Comment}                      { /* ignore */ }

    /* whitespace */
    {WhiteSpace}                   { /* ignore */ }
}

<STRING> {
    \"                                  { yybegin(YYINITIAL);
                                     return new Symbol(sym.STRING_LITERAL, stringStartRow, stringStartCol,
                                     string.toString()); }
    {HexChar}                         { string.append(hexToString(yytext())); }
    [^\n\r\"\\]+                      { string.append( yytext() ); }
    \\t                               { string.append("\\t"); }
    \\n                               { string.append("\\n"); }

    \\r                               { string.append("\\r"); }
 
    \\\"                              { string.append('\"'); }
    \\                                { string.append('\\'); }
}



/* error fallback */
[^]                                 { return symbol(sym.error,"Illegal character <"+
                                                   yytext()+">"); }
