package com.bwz6jk2227esl89ahj34;
import java_cup.runtime.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.PrintWriter;

%%
%class Lexer
%unicode
%cup
%line
%column

%eofval{
    return symbol(ParserSym.EOF);
%eofval}

%{
  public static final HashMap<Integer, String> symbolTranslation = new HashMap<Integer, String>(){{
    put(ParserSym.IF, "if");
    put(ParserSym.WHILE, "while");
    put(ParserSym.ELSE, "else");
    put(ParserSym.RETURN, "return");
    put(ParserSym.LENGTH, "length");
    put(ParserSym.INT, "int");
    put(ParserSym.BOOL, "bool");
    put(ParserSym.TRUE, "true");
    put(ParserSym.FALSE, "false");
    put(ParserSym.IDENTIFIER, "id");
    put(ParserSym.INTEGER_LITERAL, "integer");
    put(ParserSym.NOT, "!");
    put(ParserSym.TIMES, "*");
    put(ParserSym.HIGH_MULT, "*>>");
    put(ParserSym.DIVIDE, "/");
    put(ParserSym.MODULO, "%");
    put(ParserSym.PLUS, "+");
    put(ParserSym.MINUS, "-");
    put(ParserSym.LT, "<");
    put(ParserSym.LEQ, "<=");
    put(ParserSym.GEQ, ">=");
    put(ParserSym.GT, ">");
    put(ParserSym.EQUAL, "==");
    put(ParserSym.NOT_EQUAL, "!=");
    put(ParserSym.AND, "&");
    put(ParserSym.OR, "|");
    put(ParserSym.STRING_LITERAL, "string");
    put(ParserSym.EOF, "EOF");
    put(ParserSym.OPEN_PAREN, "(");
    put(ParserSym.CLOSE_PAREN, ")");
    put(ParserSym.OPEN_BRACKET, "[");
    put(ParserSym.CLOSE_BRACKET, "]");
    put(ParserSym.OPEN_BRACE, "{");
    put(ParserSym.CLOSE_BRACE, "}");
    put(ParserSym.COLON, ":");
    put(ParserSym.COMMA, ",");
    put(ParserSym.GETS, "="); 
    put(ParserSym.SEMICOLON, ";");
    put(ParserSym.CHARACTER_LITERAL, "character");
    put(ParserSym.USE, "use");
    put(ParserSym.UNDERSCORE, "_");
    put(ParserSym.error, "error:");
  }};

  public static void lexFile(String sourcePath,
                             String diagnosticPath,
                             String[] files) {
    System.out.println();
    System.out.println("Lexing " + files.length + " file(s).");
    try {
      for (String file : files) {
        if (!file.contains(".xi")) {
          System.out.println(file +
                  "is not a .xi file. This file will not be lexed.");
          continue;
        }
        System.out.println("Lexing " + sourcePath + file);
        ArrayList<String> lines = new ArrayList<>();
        FileReader reader = new FileReader(sourcePath + file);
        Lexer lexer = new Lexer(reader);
        Symbol next = lexer.next_token();
        while (next.sym != ParserSym.EOF && next.sym != ParserSym.error) {
          String line = (next.left + 1) + ":" +
                        (next.right + 1) + " " +
                        symbolTranslation.get(next.sym);
          if (next.value != null) {
            line += " " + next.value;
          }
          lines.add(line);
          next = lexer.next_token();
        }
        String output = file.replace(".xi", ".lexed");
        String writeFile = diagnosticPath + output;
        System.out.println("Writing " + diagnosticPath + output);
        Util.writeAndClose(writeFile, lines);
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
"use"                               { return symbol(ParserSym.USE); }
"if"                                { return symbol(ParserSym.IF); }
"while"                             { return symbol(ParserSym.WHILE); }
"else"                              { return symbol(ParserSym.ELSE); }
"return"                            { return symbol(ParserSym.RETURN); }
"length"                            { return symbol(ParserSym.LENGTH); }
"int"                               { return symbol(ParserSym.INT); }
"bool"                              { return symbol(ParserSym.BOOL); }
"true"                              { return symbol(ParserSym.TRUE); }
"false"                             { return symbol(ParserSym.FALSE); }

 {Identifier}                       { return symbol(ParserSym.IDENTIFIER, yytext()); }

 /* literals */
 {DecIntegerLiteral}                { if(yytext().length() > "9223372036854775808".length() || yytext().compareTo("9223372036854775808") > 0) { return symbol(ParserSym.error, "Integer literal is too big to process"); } else {return symbol(ParserSym.INTEGER_LITERAL, yytext()); } }
 \"                                 { string.setLength(0); stringStartRow = yyline; stringStartCol = yycolumn; yybegin(STRING); }

 /* char literals */
 \'[^\n\r]\'                        { return symbol(ParserSym.CHARACTER_LITERAL, yytext().charAt(1)); }
 "'\n'"                             { return symbol(ParserSym.CHARACTER_LITERAL, yytext().charAt(1)); }
 "'\r'"                             { return symbol(ParserSym.CHARACTER_LITERAL, yytext().charAt(1)); }
 \'{HexChar}\'                      { return symbol(ParserSym.CHARACTER_LITERAL, hexToString(yytext().substring(1, yytext().length()-1))); }

 \'[^\']*\'                           { return symbol(ParserSym.error, "Invalid character constant"); }

 /* terminals */
 "("                                { return symbol(ParserSym.OPEN_PAREN); }
 ")"                                { return symbol(ParserSym.CLOSE_PAREN); }
 "["                                { return symbol(ParserSym.OPEN_BRACKET); }
 "]"                                { return symbol(ParserSym.CLOSE_BRACKET); }
 "{"                                { return symbol(ParserSym.OPEN_BRACE); }
 "}"                                { return symbol(ParserSym.CLOSE_BRACE); }
 ":"                                { return symbol(ParserSym.COLON); }
 ";"                                { return symbol(ParserSym.SEMICOLON); }
 ","                                { return symbol(ParserSym.COMMA); }
 "_"                                { return symbol(ParserSym.UNDERSCORE); }

 /* operators ordered by precedence */
 "!"      { return symbol(ParserSym.NOT); }
 "*"      { return symbol(ParserSym.TIMES); }
 "*>>"    { return symbol(ParserSym.HIGH_MULT); }
 "/"      { return symbol(ParserSym.DIVIDE); }
 "%"      { return symbol(ParserSym.MODULO); }
 "+"      { return symbol(ParserSym.PLUS); }
 "-"      { return symbol(ParserSym.MINUS); }
 "<"      { return symbol(ParserSym.LT); }
 "<="     { return symbol(ParserSym.LEQ); }
 ">="     { return symbol(ParserSym.GEQ); }
 ">"      { return symbol(ParserSym.GT); }
 "=="     { return symbol(ParserSym.EQUAL); }
 "!="     { return symbol(ParserSym.NOT_EQUAL); }
 "&"      { return symbol(ParserSym.AND); }
 "|"      { return symbol(ParserSym.OR); }
 "="        { return symbol(ParserSym.GETS); }


    /* comments */
    {Comment}                      { /* ignore */ }

    /* whitespace */
    {WhiteSpace}                   { /* ignore */ }
}

<STRING> {
    \"                                  { yybegin(YYINITIAL);
                                     return new Symbol(ParserSym.STRING_LITERAL, stringStartRow, stringStartCol,
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
[^]                                 { return symbol(ParserSym.error,"Illegal character <"+
                                                   yytext()+">"); }
