
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20150326
//----------------------------------------------------


package com.bwz6jk2227esl89ahj34.ast.parse;

import com.bwz6jk2227esl89ahj34.ast.FunctionDeclaration;
import com.bwz6jk2227esl89ahj34.ast.FunctionType;
import com.bwz6jk2227esl89ahj34.ast.Identifier;
import com.bwz6jk2227esl89ahj34.ast.type.PrimitiveType;
import com.bwz6jk2227esl89ahj34.ast.type.VariableType;
import com.bwz6jk2227esl89ahj34.ast.type.VariableTypeList;
import com.bwz6jk2227esl89ahj34.util.Util;
import java_cup.runtime.Symbol;

import java.io.FileReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;


/** CUP v0.11b 20150326 generated parser.
  */
public class InterfaceParser
 extends java_cup.runtime.lr_parser {

  @Override
  public final Class<?> getSymbolContainer() {
    return ParserSym.class;
  }

  /** Default constructor. */
  @Deprecated
  public InterfaceParser() {super();}

  /** Constructor which sets the default scanner. */
  @Deprecated
  public InterfaceParser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner and a SymbolFactory. */
  public InterfaceParser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\024\000\002\002\004\000\002\002\002\000\002\003" +
    "\003\000\002\003\003\000\002\005\005\000\002\005\003" +
    "\000\002\004\004\000\002\006\005\000\002\006\003\000" +
    "\002\007\005\000\002\010\005\000\002\010\003\000\002" +
    "\011\003\000\002\011\003\000\002\012\010\000\002\012" +
    "\006\000\002\013\003\000\002\013\004\000\002\014\004" +
    "\000\002\014\003" });

  /** Access to production table. */
  @Override
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\040\000\004\004\006\001\002\000\004\002\042\001" +
    "\002\000\006\002\uffee\004\006\001\002\000\004\043\011" +
    "\001\002\000\010\002\ufff1\004\ufff1\051\010\001\002\000" +
    "\006\002\ufff0\004\ufff0\001\002\000\006\004\014\044\000" +
    "\001\002\000\004\044\033\001\002\000\004\044\ufff5\001" +
    "\002\000\004\047\021\001\002\000\006\044\ufff6\050\017" +
    "\001\002\000\004\044\ufff4\001\002\000\004\004\014\001" +
    "\002\000\004\044\ufff7\001\002\000\006\012\025\013\023" +
    "\001\002\000\006\044\ufff8\050\ufff8\001\002\000\016\002" +
    "\ufffe\004\ufffe\041\ufffe\044\ufffe\050\ufffe\051\ufffe\001\002" +
    "\000\016\002\000\004\000\041\027\044\000\050\000\051" +
    "\000\001\002\000\016\002\uffff\004\uffff\041\uffff\044\uffff" +
    "\050\uffff\051\uffff\001\002\000\014\002\ufffb\004\ufffb\044" +
    "\ufffb\050\ufffb\051\ufffb\001\002\000\004\042\031\001\002" +
    "\000\014\002\ufffc\004\ufffc\044\ufffc\050\ufffc\051\ufffc\001" +
    "\002\000\016\002\000\004\000\041\027\044\000\050\000" +
    "\051\000\001\002\000\014\002\ufffd\004\ufffd\044\ufffd\050" +
    "\ufffd\051\ufffd\001\002\000\012\002\ufff2\004\ufff2\047\034" +
    "\051\ufff2\001\002\000\006\012\025\013\023\001\002\000" +
    "\012\002\ufff9\004\ufff9\050\037\051\ufff9\001\002\000\010" +
    "\002\ufff3\004\ufff3\051\ufff3\001\002\000\006\012\025\013" +
    "\023\001\002\000\010\002\ufffa\004\ufffa\051\ufffa\001\002" +
    "\000\004\002\uffef\001\002\000\004\002\001\001\002" });

  /** Access to parse-action table. */
  @Override
  public short[][] action_table() {return _action_table;}

  /** {@code reduce_goto} table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\040\000\010\012\006\013\004\014\003\001\001\000" +
    "\002\001\001\000\010\012\006\013\004\014\040\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\012\002\015\007\014\010\012\011\011\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\006\007\014\010\017\001\001" +
    "\000\002\001\001\000\006\003\023\004\021\001\001\000" +
    "\002\001\001\000\002\001\001\000\006\002\027\005\025" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\006\002\027\005\031\001\001" +
    "\000\002\001\001\000\002\001\001\000\010\003\023\004" +
    "\034\006\035\001\001\000\002\001\001\000\002\001\001" +
    "\000\010\003\023\004\034\006\037\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001" });

  /** Access to {@code reduce_goto} table. */
  @Override
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$InterfaceParser$actions action_obj;

  /** Action encapsulation object initializer. */
  @Override
  protected void init_actions()
    {
      action_obj = new CUP$InterfaceParser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  @Override
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack<java_cup.runtime.Symbol> stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$InterfaceParser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  @Override
  public int start_state() {return 0;}
  /** Indicates start production. */
  @Override
  public int start_production() {return 0;}

  /** {@code EOF} Symbol index. */
  @Override
  public int EOF_sym() {return 0;}

  /** {@code error} Symbol index. */
  @Override
  public int error_sym() {return 1;}




    /**
     * Parses an interface file and adds the declarations to a given list
     * @param libPath       a String of the path to the interface files
     * @param interfaceName a String of the name of the interface
     * @param declarations  a List<FunctionDeclaration> to store the parsed function declarations that must be empty
     * @return              a String of the error or null if no error
     */
    public static String parseInterface(String libPath, String interfaceName, List<FunctionDeclaration> declarations) {
        try {
            FileReader reader = new FileReader(libPath + interfaceName + ".ixi");
            Lexer lexer = new Lexer(reader);
            InterfaceParser parser = new InterfaceParser(lexer);
            Symbol result = parser.parse();
            if (parser.hasSyntaxError) {
                // Encountered parsing error, return error message
                return parser.syntaxErrMessage;
            }
            declarations.addAll((List<FunctionDeclaration>) result.value);
        } catch(Exception e) {
            return "Interface " + interfaceName + " not found";
        }
        return null;
    }

    public boolean hasSyntaxError = false;
    public String syntaxErrMessage = "";

    public void syntax_error(java_cup.runtime.Symbol cur_token){
        hasSyntaxError = true;
        syntaxErrMessage = cur_token.left + ":" + cur_token.right +
          " error: Unexpected token " + Util.symbolTranslation.get(cur_token.sym);
        if (cur_token.value != null) {
            syntaxErrMessage += " " + cur_token.value;
        }
        done_parsing();
    }


/** Cup generated class to encapsulate user supplied action code.*/
class CUP$InterfaceParser$actions {
    private final InterfaceParser parser;

    /** Constructor */
    CUP$InterfaceParser$actions(InterfaceParser parser) {
        this.parser = parser;
    }

    /** Method with the actual generated action code for actions 0 to 19. */
    public final java_cup.runtime.Symbol CUP$InterfaceParser$do_action_part00000000(
            int                        CUP$InterfaceParser$act_num,
            java_cup.runtime.lr_parser CUP$InterfaceParser$parser,
            java.util.Stack<java_cup.runtime.Symbol> CUP$InterfaceParser$stack,
            int                        CUP$InterfaceParser$top)
            throws java.lang.Exception {
            /* Symbol object for return from actions */
            java_cup.runtime.Symbol CUP$InterfaceParser$result;

        /* select the action based on the action number */
        switch (CUP$InterfaceParser$act_num) {
        /*. . . . . . . . . . . . . . . . . . . .*/
        case 0: // $START ::= function_declaration_list EOF 
            {
                Object RESULT = null;
                int start_valleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).left;
                int start_valright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).right;
                List<FunctionDeclaration> start_val = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).<List<FunctionDeclaration>> value();
                RESULT = start_val;
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("$START",0, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            /* ACCEPT */
            CUP$InterfaceParser$parser.done_parsing();
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 1: // empty ::= 
            {
                Object RESULT = null;

                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("empty",0, CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 2: // primitive_type ::= INT 
            {
                PrimitiveType RESULT = null;
                 RESULT = PrimitiveType.INT; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("primitive_type",1, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 3: // primitive_type ::= BOOL 
            {
                PrimitiveType RESULT = null;
                 RESULT = PrimitiveType.BOOL; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("primitive_type",1, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 4: // array_no_size_type ::= OPEN_BRACKET CLOSE_BRACKET array_no_size_type 
            {
                Integer RESULT = null;
                int anstleft = CUP$InterfaceParser$stack.peek().left;
                int anstright = CUP$InterfaceParser$stack.peek().right;
                Integer anst = CUP$InterfaceParser$stack.peek().<Integer> value();
                 RESULT = anst + 1; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("array_no_size_type",3, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 5: // array_no_size_type ::= empty 
            {
                Integer RESULT = null;
                 RESULT = 0; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("array_no_size_type",3, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 6: // var_type ::= primitive_type array_no_size_type 
            {
                VariableType RESULT = null;
                int ptleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).left;
                int ptright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).right;
                PrimitiveType pt = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).<PrimitiveType> value();
                int anstleft = CUP$InterfaceParser$stack.peek().left;
                int anstright = CUP$InterfaceParser$stack.peek().right;
                Integer anst = CUP$InterfaceParser$stack.peek().<Integer> value();
                 RESULT = new VariableType(pt, anst); 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("var_type",2, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 7: // function_declaration_return_types ::= var_type COMMA function_declaration_return_types 
            {
                List<VariableType> RESULT = null;
                int tleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).left;
                int tright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).right;
                VariableType t = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).<VariableType> value();
                int lstleft = CUP$InterfaceParser$stack.peek().left;
                int lstright = CUP$InterfaceParser$stack.peek().right;
                List<VariableType> lst = CUP$InterfaceParser$stack.peek().<List<VariableType>> value();
                 lst.add(0,t); RESULT = lst; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_return_types",4, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 8: // function_declaration_return_types ::= var_type 
            {
                List<VariableType> RESULT = null;
                int tleft = CUP$InterfaceParser$stack.peek().left;
                int tright = CUP$InterfaceParser$stack.peek().right;
                VariableType t = CUP$InterfaceParser$stack.peek().<VariableType> value();
                 List<VariableType> lst = new LinkedList<>(); lst.add(0, t); RESULT = lst; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_return_types",4, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 9: // function_declaration_argument_single ::= IDENTIFIER COLON var_type 
            {
                SimpleEntry<Identifier,VariableType> RESULT = null;
                int ileft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).left;
                int iright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).right;
                String i = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).<String> value();
                int tleft = CUP$InterfaceParser$stack.peek().left;
                int tright = CUP$InterfaceParser$stack.peek().right;
                VariableType t = CUP$InterfaceParser$stack.peek().<VariableType> value();
                 RESULT = new SimpleEntry<Identifier, VariableType>(new Identifier(i), t); 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_argument_single",5, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 10: // function_declaration_argument_list ::= function_declaration_argument_single COMMA function_declaration_argument_list 
            {
                List<SimpleEntry<Identifier,VariableType>> RESULT = null;
                int fdasleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).left;
                int fdasright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).right;
                SimpleEntry<Identifier,VariableType> fdas = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2).<SimpleEntry<Identifier,VariableType>> value();
                int lstleft = CUP$InterfaceParser$stack.peek().left;
                int lstright = CUP$InterfaceParser$stack.peek().right;
                List<SimpleEntry<Identifier,VariableType>> lst = CUP$InterfaceParser$stack.peek().<List<SimpleEntry<Identifier,VariableType>>> value();
                 lst.add(0, fdas); RESULT = lst; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_argument_list",6, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-2), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 11: // function_declaration_argument_list ::= function_declaration_argument_single 
            {
                List<SimpleEntry<Identifier,VariableType>> RESULT = null;
                int fdasleft = CUP$InterfaceParser$stack.peek().left;
                int fdasright = CUP$InterfaceParser$stack.peek().right;
                SimpleEntry<Identifier,VariableType> fdas = CUP$InterfaceParser$stack.peek().<SimpleEntry<Identifier,VariableType>> value();
                 List<SimpleEntry<Identifier, VariableType>> lst = new LinkedList<>(); lst.add(0, fdas); RESULT = lst; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_argument_list",6, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 12: // function_declaration_argument ::= function_declaration_argument_list 
            {
                List<SimpleEntry<Identifier,VariableType>> RESULT = null;
                int lstleft = CUP$InterfaceParser$stack.peek().left;
                int lstright = CUP$InterfaceParser$stack.peek().right;
                List<SimpleEntry<Identifier,VariableType>> lst = CUP$InterfaceParser$stack.peek().<List<SimpleEntry<Identifier,VariableType>>> value();
                 RESULT = lst; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_argument",7, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 13: // function_declaration_argument ::= empty 
            {
                List<SimpleEntry<Identifier,VariableType>> RESULT = null;
                 RESULT = new LinkedList<>(); 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_argument",7, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 14: // function_declaration ::= IDENTIFIER OPEN_PAREN function_declaration_argument CLOSE_PAREN COLON function_declaration_return_types 
            {
                FunctionDeclaration RESULT = null;
                int idleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-5).left;
                int idright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-5).right;
                String id = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-5).<String> value();
                int argPairsleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-3).left;
                int argPairsright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-3).right;
                List<SimpleEntry<Identifier,VariableType>> argPairs = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-3).<List<SimpleEntry<Identifier,VariableType>>> value();
                int tleft = CUP$InterfaceParser$stack.peek().left;
                int tright = CUP$InterfaceParser$stack.peek().right;
                List<VariableType> t = CUP$InterfaceParser$stack.peek().<List<VariableType>> value();
                 List<Identifier> args = new LinkedList<>(); List<VariableType> argTypes = new LinkedList<>(); for (SimpleEntry<Identifier, VariableType> se : argPairs) { args.add(se.getKey()); argTypes.add(se.getValue()); } RESULT = new FunctionDeclaration(new Identifier(id), new FunctionType(argTypes, new VariableTypeList(t)), args, null); 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration",8, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-5), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 15: // function_declaration ::= IDENTIFIER OPEN_PAREN function_declaration_argument CLOSE_PAREN 
            {
                FunctionDeclaration RESULT = null;
                int idleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-3).left;
                int idright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-3).right;
                String id = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-3).<String> value();
                int argPairsleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).left;
                int argPairsright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).right;
                List<SimpleEntry<Identifier,VariableType>> argPairs = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).<List<SimpleEntry<Identifier,VariableType>>> value();
                 List<Identifier> args = new LinkedList<>(); List<VariableType> argTypes = new LinkedList<>(); for (SimpleEntry<Identifier, VariableType> se : argPairs) { args.add(se.getKey()); argTypes.add(se.getValue()); } RESULT = new FunctionDeclaration(new Identifier(id), new FunctionType(argTypes, new VariableTypeList(new LinkedList<>())), args, null); 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration",8, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-3), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 16: // function_declaration_line ::= function_declaration 
            {
                FunctionDeclaration RESULT = null;
                int fdleft = CUP$InterfaceParser$stack.peek().left;
                int fdright = CUP$InterfaceParser$stack.peek().right;
                FunctionDeclaration fd = CUP$InterfaceParser$stack.peek().<FunctionDeclaration> value();
                 RESULT = fd; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_line",9, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 17: // function_declaration_line ::= function_declaration SEMICOLON 
            {
                FunctionDeclaration RESULT = null;
                int fdleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).left;
                int fdright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).right;
                FunctionDeclaration fd = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).<FunctionDeclaration> value();
                 RESULT = fd; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_line",9, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 18: // function_declaration_list ::= function_declaration_line function_declaration_list 
            {
                List<FunctionDeclaration> RESULT = null;
                int fdleft = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).left;
                int fdright = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).right;
                FunctionDeclaration fd = CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1).<FunctionDeclaration> value();
                int lstleft = CUP$InterfaceParser$stack.peek().left;
                int lstright = CUP$InterfaceParser$stack.peek().right;
                List<FunctionDeclaration> lst = CUP$InterfaceParser$stack.peek().<List<FunctionDeclaration>> value();
                 lst.add(0, fd); RESULT = lst; 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_list",10, CUP$InterfaceParser$stack.elementAt(CUP$InterfaceParser$top-1), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /*. . . . . . . . . . . . . . . . . . . .*/
        case 19: // function_declaration_list ::= function_declaration_line 
            {
                List<FunctionDeclaration> RESULT = null;
                int fdleft = CUP$InterfaceParser$stack.peek().left;
                int fdright = CUP$InterfaceParser$stack.peek().right;
                FunctionDeclaration fd = CUP$InterfaceParser$stack.peek().<FunctionDeclaration> value();
                 RESULT = new LinkedList<FunctionDeclaration>(); RESULT.add(fd); 
                CUP$InterfaceParser$result = parser.getSymbolFactory().newSymbol("function_declaration_list",10, CUP$InterfaceParser$stack.peek(), CUP$InterfaceParser$stack.peek(), RESULT);
            }
            return CUP$InterfaceParser$result;

        /* . . . . . .*/
        default:
            throw new Exception(
                  "Invalid action number " + CUP$InterfaceParser$act_num + " found in internal parse table");

        }
    } /* end of method */

    /** Method splitting the generated action code into several parts. */
    public final java_cup.runtime.Symbol CUP$InterfaceParser$do_action(
            int                        CUP$InterfaceParser$act_num,
            java_cup.runtime.lr_parser CUP$InterfaceParser$parser,
            java.util.Stack<java_cup.runtime.Symbol> CUP$InterfaceParser$stack,
            int                        CUP$InterfaceParser$top)
            throws java.lang.Exception {
            return CUP$InterfaceParser$do_action_part00000000(
                           CUP$InterfaceParser$act_num,
                           CUP$InterfaceParser$parser,
                           CUP$InterfaceParser$stack,
                           CUP$InterfaceParser$top);
    }
}

}
