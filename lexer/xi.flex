package com.bwz6jk2227esl89ahj34;
import java_cup.runtime.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.PrintWriter;

%%
%public
%class Lexer
%unicode
%cup
%line
%column

%eofval{
    return symbol(ParserSym.EOF);
%eofval}

%{
  StringBuffer string = new StringBuffer();
  int stringStartCol = -1;
  int stringStartRow = -1;
  StringBuffer hexBuffer = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline + 1, yycolumn + 1);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline + 1, yycolumn + 1, value);
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
DecIntegerLiteralNegativeBound = 9223372036854775808
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
"int"                               { return symbol(ParserSym.INT); }
"bool"                              { return symbol(ParserSym.BOOL); }
"true"                              { return symbol(ParserSym.TRUE); }
"false"                             { return symbol(ParserSym.FALSE); }
"length"                            { return symbol(ParserSym.LENGTH); }
 {Identifier}                       { return symbol(ParserSym.IDENTIFIER, yytext()); }

 /* literals */
 {DecIntegerLiteralNegativeBound}   { return symbol(ParserSym.NEGATIVE_INT_BOUND); }
 {DecIntegerLiteral}                { if(yytext().length() > "9223372036854775807".length() || (yytext().length() == "9223372036854775807".length() && yytext().compareTo("9223372036854775807") > 0)) { return symbol(ParserSym.error, "Integer literal is too big to process"); } else {return symbol(ParserSym.INTEGER_LITERAL, yytext()); } }
 \"                                 { string.setLength(0); stringStartRow = yyline + 1; stringStartCol = yycolumn + 1; yybegin(STRING); }

 /* char literals */
 \'[^\n\r]\'                        { return symbol(ParserSym.CHARACTER_LITERAL, yytext().charAt(1)); }
 "'\\n'"                            { return symbol(ParserSym.CHARACTER_LITERAL, '\n'); }
 "'\\r'"                            { return symbol(ParserSym.CHARACTER_LITERAL, '\r'); }
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
