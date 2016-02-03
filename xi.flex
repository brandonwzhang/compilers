
%%
%class XiLexer
%unicode
%standalone
%line
%column

%{
   	public enum Sym{
		IF, 
		WHILE,
		ELSE,
		RETURN,
		LENGTH,
		INT,
		BOOL,
		TRUE,
		FALSE,
		IDENTIFIER,
		INTEGER_LITERAL,
		LOGICAL_NEG,
		TIMES,
		HIGH_MULT,
		DIVIDE,
		MOD,
		PLUS,
		MINUS,
		LESS,
		LESS_EQ,
		GREATER_EQ,
		GREATER,
		EQEQ,
		NOT_EQ,
		AND,
		OR,
		STRING_LITERAL

   }

   StringBuffer string = new StringBuffer();

   private Symbol Symbol(int type) {
       return new Symbol(type, yyline, yycolumn);
   }
   private Symbol Symbol(int type, Object value) {
       return new Symbol(type, yyline, yycolumn, value);
   }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

Comment = "//" {InputCharacter}* {LineTerminator}?
Letter = [A-Za-z]
Identifier = {Letter} ({Letter} | [0-9_'])*
DecIntegerLiteral = 0 | [1-9][0-9]*

%state STRING
%%

<YYINITIAL> {
 /* keywords */
"if"            	{ return Symbol(Sym.IF); }
"while"         	{ return Symbol(Sym.WHILE); }
"else"          	{ return Symbol(Sym.ELSE); }
"return"        	{ return Symbol(Sym.RETURN); }
"length"        	{ return Symbol(Sym.LENGTH); }
"int"           	{ return Symbol(Sym.INT); }
"bool"          	{ return Symbol(Sym.BOOL); }
"true"          	{ return Symbol(Sym.TRUE); }
"false"         	{ return Symbol(Sym.FALSE); }

 {Identifier}                   	{ return Symbol(Sym.IDENTIFIER); }

 /* literals */
 {DecIntegerLiteral}            	{ return Symbol(Sym.INTEGER_LITERAL); }
 \"                             		{ string.setLength(0); yybegin(STRING); }

 /* operators ordered by precedence */
 "!" 		{ return Symbol(Sym.LOGICAL_NEG); }
 "*" 		{ return Symbol(Sym.TIMES); }
 "*>>" 		{ return Symbol(Sym.HIGH_MULT); }
 "/" 		{ return Symbol(Sym.DIVIDE); }
 "%" 		{ return Symbol(Sym.MOD); }
 "+"                  { return Symbol(Sym.PLUS); }
 "-"                   { return Symbol(Sym.MINUS); }
 "<" 		{ return Symbol(Sym.LESS); }
 "<=" 		{ return Symbol(Sym.LESS_EQ); }
 ">="		{ return Symbol(Sym.GREATER_EQ); }
 ">" 		{ return Symbol(Sym.GREATER); }
 "=="                { return Symbol(Sym.EQEQ); }
 "!=" 		{ return Symbol(Sym.NOT_EQ); }
 "&" 		{ return Symbol(Sym.AND); }
 "|" 		{ return Symbol(Sym.OR); }

 /* comments */
 {Comment}                      { /* ignore */ }

 /* whitespace */
 {WhiteSpace}                   { /* ignore */ }
}

<STRING> {
 \"			{ yybegin(YYINITIAL);
                                  	  return Symbol(Sym.STRING_LITERAL,
                                     string.toString()); }
 [^\n\r\"\\]+               	{ string.append( yytext() ); }
 \\t                            	{ string.append('\t'); }
 \\n                            	{ string.append('\n'); }

 \\r                            	{ string.append('\r'); }
 \\\"                           	{ string.append('\"'); }
 \\                             	{ string.append('\\'); }
}

/* error fallback */
[^]                              { throw new Error("Illegal character <"+
                                                   yytext()+">"); }

