
import java_cup.runtime.*;

%%
%class XiLexer
%unicode
%cup
%line
%column

%{
   StringBuffer string = new StringBuffer();

   private symbol symbol(int type) {
       return new symbol(type, yyline, yycolumn);
   }
   private symbol symbol(int type, Object value) {
       return new symbol(type, yyline, yycolumn, value);
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

%state STRING
%state CHAR
%%

<YYINITIAL> {
 /* keywords */
"if"            	{ return symbol(sym.IF); }
"while"         	{ return symbol(sym.WHILE); }
"else"          	{ return symbol(sym.ELSE); }
"return"        	{ return symbol(sym.RETURN); }
"length"        	{ return symbol(sym.LENGTH); }
"int"           	{ return symbol(sym.INT); }
"bool"          	{ return symbol(sym.BOOL); }
"true"          	{ return symbol(sym.TRUE); }
"false"         	{ return symbol(sym.FALSE); }

 {Identifier}                   	{ return symbol(sym.IDENTIFIER, yytext()); }

 /* literals */
 {DecIntegerLiteral}                { return symbol(sym.INTEGER_LITERAL, yytext()); }
 \"                             	{ string.setLength(0); yybegin(STRING); }

 /* char literals */
 \'[^\n\r]\'                        { return symbol(sym.CHAR_LITERAL, yytext().charAt(1)); }
 "'\n'"                             { return symbol(sym.CHAR_LITERAL, yytext().charAt(1)); }
 "'\r'"                             { return symbol(sym.CHAR_LITERAL, yytext().charAt(1)); }


 /* terminals */
 "("        { return symbol(sym.LEFT_PAREN); }
 ")"        { return symbol(sym.RIGHT_PAREN); }
 "["        { return symbol(sym.LEFT_SQUARE_BRACKET); }
 "]"        { return symbol(sym.RIGHT_SQUARE_BRACKET); }
 "{"        { return symbol(sym.LEFT_CURLY_BRACKET); }
 "}"        { return symbol(sym.RIGHT_CURLY_BRACKET); }
 "."        { return symbol(sym.PERIOD); }
 ":"        { return symbol(sym.COLON); }
 ";"        { return symbol(sym.SEMICOLON); }
 ","        { return symbol(sym.COMMA); }


 /* operators ordered by precedence */
 "!" 		{ return symbol(sym.LOGICAL_NEG); }
 "*" 		{ return symbol(sym.TIMES); }
 "*>>" 		{ return symbol(sym.HIGH_MULT); }
 "/" 		{ return symbol(sym.DIVIDE); }
 "%" 		{ return symbol(sym.MOD); }
 "+"        { return symbol(sym.PLUS); }
 "-"        { return symbol(sym.MINUS); }
 "<" 		{ return symbol(sym.LESS); }
 "<=" 		{ return symbol(sym.LESS_EQ); }
 ">="		{ return symbol(sym.GREATER_EQ); }
 ">" 		{ return symbol(sym.GREATER); }
 "=="       { return symbol(sym.EQEQ); }
 "!=" 		{ return symbol(sym.NOT_EQ); }
 "&" 		{ return symbol(sym.AND); }
 "|" 		{ return symbol(sym.OR); }
 "="        { return symbol(sym.ASSIGNMENT); }


 /* comments */
 {Comment}                      { /* ignore */ }

 /* whitespace */
 {WhiteSpace}                   { /* ignore */ }
}

<STRING> {
 \"			                        { yybegin(YYINITIAL);
                                  	  return symbol(sym.STRING_LITERAL,
                                     string.toString()); }
 [^\n\r\"\\]+               	    { string.append( yytext() ); }
 \\t                            	{ string.append('\t'); }
 \\n                            	{ string.append('\n'); }

 \\r                            	{ string.append('\r'); }
 \\\"                           	{ string.append('\"'); }
 \\                             	{ string.append('\\'); }
}



/* error fallback */
[^]                                 { throw new Error("Illegal character <"+
                                                   yytext()+">"); }
