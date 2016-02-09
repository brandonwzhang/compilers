package com.bwz6jk2227esl89ahj34;
import java_cup.runtime.*;

%%
%class XiLexer
%unicode
%cup
%line
%column

%{
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
CharLiteral = \'

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
 {DecIntegerLiteral}                { if(yytext().compareTo("9223372036854775807") > 0) { return symbol(sym.ERROR, "Integer literal is too big to process"); } else {return symbol(sym.INTEGER_LITERAL, yytext()); } }
 \"                                 { string.setLength(0); stringStartRow = yyline; stringStartCol = yycolumn; yybegin(STRING); }

 /* char literals */
 \'[^\n\r]\'                        { return symbol(sym.CHAR_LITERAL, yytext().charAt(1)); }
 "'\n'"                             { return symbol(sym.CHAR_LITERAL, yytext().charAt(1)); }
 "'\r'"                             { return symbol(sym.CHAR_LITERAL, yytext().charAt(1)); }
 \'{HexChar}\'                      { return symbol(sym.CHAR_LITERAL, hexToString(yytext().substring(1, yytext().length()-1))); }

 \'[^]*\'                           { return symbol(sym.ERROR, "Invalid character constant"); }

 /* terminals */
 "("                                { return symbol(sym.LEFT_PAREN); }
 ")"                                { return symbol(sym.RIGHT_PAREN); }
 "["                                { return symbol(sym.LEFT_SQUARE_BRACKET); }
 "]"                                { return symbol(sym.RIGHT_SQUARE_BRACKET); }
 "{"                                { return symbol(sym.LEFT_CURLY_BRACKET); }
 "}"                                { return symbol(sym.RIGHT_CURLY_BRACKET); }
 "."                                { return symbol(sym.PERIOD); }
 ":"                                { return symbol(sym.COLON); }
 ";"                                { return symbol(sym.SEMICOLON); }
 ","                                { return symbol(sym.COMMA); }
 "_"                                { return symbol(sym.UNDERSCORE); }

 /* operators ordered by precedence */
 "!"      { return symbol(sym.LOGICAL_NEG); }
 "*"      { return symbol(sym.TIMES); }
 "*>>"    { return symbol(sym.HIGH_MULT); }
 "/"      { return symbol(sym.DIVIDE); }
 "%"      { return symbol(sym.MOD); }
 "+"      { return symbol(sym.PLUS); }
 "-"      { return symbol(sym.MINUS); }
 "<"      { return symbol(sym.LESS); }
 "<="     { return symbol(sym.LESS_EQ); }
 ">="     { return symbol(sym.GREATER_EQ); }
 ">"      { return symbol(sym.GREATER); }
 "=="     { return symbol(sym.EQEQ); }
 "!="     { return symbol(sym.NOT_EQ); }
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
[^]                                 { return symbol(sym.ERROR,"Illegal character <"+
                                                   yytext()+">"); }
