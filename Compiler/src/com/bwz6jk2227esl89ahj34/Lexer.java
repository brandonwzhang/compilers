/* The following code was generated by JFlex 1.6.1 */

package com.bwz6jk2227esl89ahj34;
import java_cup.runtime.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.PrintWriter;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.6.1
 * from the specification file <tt>/Users/eric/Documents/compilers/lexer/xi.flex</tt>
 */
class Lexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int STRING = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1, 1
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\3\1\2\1\0\1\3\1\1\22\0\1\3\1\47\1\34"+
    "\2\0\1\52\1\57\1\35\1\36\1\37\1\50\1\53\1\46\1\54"+
    "\1\0\1\4\1\7\1\10\6\13\2\10\1\44\1\45\1\55\1\56"+
    "\1\51\2\0\5\14\25\5\1\40\1\11\1\41\1\0\1\6\1\0"+
    "\1\33\1\31\2\5\1\17\1\21\1\30\1\23\1\20\2\5\1\24"+
    "\1\5\1\27\1\32\2\5\1\25\1\16\1\26\1\15\1\5\1\22"+
    "\1\12\2\5\1\42\1\60\1\43\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uff92\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\2\2\1\3\1\4\1\5\2\6\11\4"+
    "\1\7\1\1\1\10\1\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25"+
    "\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\35"+
    "\1\2\2\4\1\36\7\4\3\0\1\37\1\40\1\0"+
    "\1\41\1\42\1\43\1\0\1\44\1\45\1\46\1\47"+
    "\1\50\1\4\1\51\6\4\1\52\1\37\3\0\1\53"+
    "\1\0\1\54\4\4\1\55\1\56\1\0\1\57\1\60"+
    "\1\61\1\62\1\63\2\4\1\0\1\64\1\65\1\66";

  private static int [] zzUnpackAction() {
    int [] result = new int[103];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\61\0\142\0\223\0\142\0\304\0\365\0\142"+
    "\0\142\0\u0126\0\u0157\0\u0188\0\u01b9\0\u01ea\0\u021b\0\u024c"+
    "\0\u027d\0\u02ae\0\u02df\0\142\0\u0310\0\142\0\142\0\142"+
    "\0\142\0\142\0\142\0\142\0\142\0\142\0\u0341\0\u0372"+
    "\0\u03a3\0\142\0\142\0\142\0\u03d4\0\u0405\0\142\0\142"+
    "\0\u0436\0\u0467\0\142\0\u0498\0\u04c9\0\u04fa\0\365\0\u052b"+
    "\0\u055c\0\u058d\0\u05be\0\u05ef\0\u0620\0\u0651\0\u0682\0\u06b3"+
    "\0\u06e4\0\u0715\0\142\0\u0746\0\142\0\142\0\142\0\u0777"+
    "\0\142\0\142\0\142\0\142\0\365\0\u07a8\0\365\0\u07d9"+
    "\0\u080a\0\u083b\0\u086c\0\u089d\0\u08ce\0\142\0\142\0\u08ff"+
    "\0\u0930\0\u0961\0\142\0\u0992\0\365\0\u09c3\0\u09f4\0\u0a25"+
    "\0\u0a56\0\365\0\365\0\u0a87\0\142\0\142\0\142\0\365"+
    "\0\365\0\u0ab8\0\u0ae9\0\u0b1a\0\365\0\365\0\142";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[103];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\2\5\1\6\1\7\1\10\1\11\1\12"+
    "\1\3\1\7\1\12\1\7\1\13\1\7\1\14\1\15"+
    "\1\16\1\17\1\7\1\20\1\21\1\22\2\7\1\23"+
    "\2\7\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
    "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\1\44\1\45\1\46\1\47\1\50\1\51\2\3"+
    "\6\51\1\52\22\51\1\53\24\51\63\0\1\5\62\0"+
    "\1\54\61\0\4\7\1\0\22\7\1\0\1\7\32\0"+
    "\2\12\2\0\1\12\52\0\4\7\1\0\4\7\1\55"+
    "\15\7\1\0\1\7\30\0\4\7\1\0\12\7\1\56"+
    "\7\7\1\0\1\7\30\0\4\7\1\0\7\7\1\57"+
    "\5\7\1\60\4\7\1\0\1\7\30\0\4\7\1\0"+
    "\21\7\1\61\1\0\1\7\30\0\4\7\1\0\11\7"+
    "\1\62\10\7\1\0\1\7\30\0\4\7\1\0\5\7"+
    "\1\63\14\7\1\0\1\7\30\0\4\7\1\0\5\7"+
    "\1\64\14\7\1\0\1\7\30\0\4\7\1\0\13\7"+
    "\1\65\6\7\1\0\1\7\30\0\4\7\1\0\20\7"+
    "\1\66\1\7\1\0\1\7\23\0\1\67\2\70\6\67"+
    "\1\71\23\67\1\72\23\67\56\0\1\73\53\0\1\74"+
    "\65\0\1\75\60\0\1\76\60\0\1\77\2\0\1\51"+
    "\2\0\6\51\1\0\22\51\1\0\24\51\12\0\1\100"+
    "\12\0\1\101\1\102\1\103\4\0\1\104\24\0\1\54"+
    "\1\4\1\5\56\54\5\0\4\7\1\0\5\7\1\105"+
    "\14\7\1\0\1\7\30\0\4\7\1\0\4\7\1\106"+
    "\15\7\1\0\1\7\30\0\4\7\1\0\14\7\1\107"+
    "\5\7\1\0\1\7\30\0\4\7\1\0\12\7\1\110"+
    "\7\7\1\0\1\7\30\0\4\7\1\0\6\7\1\111"+
    "\13\7\1\0\1\7\30\0\4\7\1\0\15\7\1\112"+
    "\4\7\1\0\1\7\30\0\4\7\1\0\14\7\1\113"+
    "\5\7\1\0\1\7\30\0\4\7\1\0\3\7\1\114"+
    "\16\7\1\0\1\7\30\0\4\7\1\0\20\7\1\115"+
    "\1\7\1\0\1\7\23\0\35\70\1\116\60\70\1\117"+
    "\35\70\1\120\12\70\1\121\1\70\1\122\5\70\1\116"+
    "\23\70\35\0\1\116\74\0\1\123\22\0\1\124\52\0"+
    "\4\7\1\0\5\7\1\125\14\7\1\0\1\7\30\0"+
    "\4\7\1\0\4\7\1\126\15\7\1\0\1\7\30\0"+
    "\4\7\1\0\12\7\1\127\7\7\1\0\1\7\30\0"+
    "\4\7\1\0\16\7\1\130\3\7\1\0\1\7\30\0"+
    "\4\7\1\0\3\7\1\131\16\7\1\0\1\7\30\0"+
    "\4\7\1\0\5\7\1\132\14\7\1\0\1\7\30\0"+
    "\4\7\1\0\12\7\1\133\7\7\1\0\1\7\23\0"+
    "\13\70\1\134\21\70\1\117\60\70\1\135\60\70\1\136"+
    "\23\70\7\0\2\137\2\0\2\137\51\0\4\7\1\0"+
    "\5\7\1\140\14\7\1\0\1\7\30\0\4\7\1\0"+
    "\5\7\1\141\14\7\1\0\1\7\30\0\4\7\1\0"+
    "\14\7\1\142\5\7\1\0\1\7\30\0\4\7\1\0"+
    "\13\7\1\143\6\7\1\0\1\7\23\0\7\70\2\144"+
    "\2\70\2\144\20\70\1\117\23\70\5\0\4\7\1\0"+
    "\11\7\1\145\10\7\1\0\1\7\30\0\4\7\1\0"+
    "\15\7\1\146\4\7\1\0\1\7\23\0\35\70\1\147"+
    "\23\70";

  private static int [] zzUnpackTrans() {
    int [] result = new int[2891];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\11\1\1\1\11\2\1\2\11\12\1\1\11"+
    "\1\1\11\11\3\1\3\11\2\1\2\11\2\1\1\11"+
    "\13\1\3\0\1\1\1\11\1\0\3\11\1\0\4\11"+
    "\11\1\2\11\3\0\1\11\1\0\7\1\1\0\3\11"+
    "\4\1\1\0\2\1\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[103];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /* user code: */
  public static void lexFile(String sourcePath,
                             String diagnosticPath,
                             String[] files) {
    try {
      for (String file : files) {
        if (!file.contains(".xi")) {
          System.out.println(file +
                  "is not a .xi file. This file will not be lexed.");
          continue;
        }
        ArrayList<String> lines = new ArrayList<>();
        FileReader reader = new FileReader(sourcePath + file);
        Lexer lexer = new Lexer(reader);
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
            break;
          }
          next = lexer.next_token();
        }

        String output = file.replace(".xi", ".lexed");
        String writeFile = diagnosticPath + output;
        Util.makePath(writeFile.substring(0, writeFile.lastIndexOf('/') + 1));
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


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  Lexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 164) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            zzDoEOF();
          {     return symbol(ParserSym.EOF);
 }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { return symbol(ParserSym.error,"Illegal character <"+
                                                   yytext()+">");
            }
          case 55: break;
          case 2: 
            { /* ignore */
            }
          case 56: break;
          case 3: 
            { return symbol(ParserSym.DIVIDE);
            }
          case 57: break;
          case 4: 
            { return symbol(ParserSym.IDENTIFIER, yytext());
            }
          case 58: break;
          case 5: 
            { return symbol(ParserSym.UNDERSCORE);
            }
          case 59: break;
          case 6: 
            { if(yytext().length() > "9223372036854775808".length() || yytext().compareTo("9223372036854775808") > 0) { return symbol(ParserSym.error, "Integer literal is too big to process"); } else {return symbol(ParserSym.INTEGER_LITERAL, yytext()); }
            }
          case 60: break;
          case 7: 
            { string.setLength(0); stringStartRow = yyline + 1; stringStartCol = yycolumn + 1; yybegin(STRING);
            }
          case 61: break;
          case 8: 
            { return symbol(ParserSym.OPEN_PAREN);
            }
          case 62: break;
          case 9: 
            { return symbol(ParserSym.CLOSE_PAREN);
            }
          case 63: break;
          case 10: 
            { return symbol(ParserSym.OPEN_BRACKET);
            }
          case 64: break;
          case 11: 
            { return symbol(ParserSym.CLOSE_BRACKET);
            }
          case 65: break;
          case 12: 
            { return symbol(ParserSym.OPEN_BRACE);
            }
          case 66: break;
          case 13: 
            { return symbol(ParserSym.CLOSE_BRACE);
            }
          case 67: break;
          case 14: 
            { return symbol(ParserSym.COLON);
            }
          case 68: break;
          case 15: 
            { return symbol(ParserSym.SEMICOLON);
            }
          case 69: break;
          case 16: 
            { return symbol(ParserSym.COMMA);
            }
          case 70: break;
          case 17: 
            { return symbol(ParserSym.NOT);
            }
          case 71: break;
          case 18: 
            { return symbol(ParserSym.TIMES);
            }
          case 72: break;
          case 19: 
            { return symbol(ParserSym.GT);
            }
          case 73: break;
          case 20: 
            { return symbol(ParserSym.MODULO);
            }
          case 74: break;
          case 21: 
            { return symbol(ParserSym.PLUS);
            }
          case 75: break;
          case 22: 
            { return symbol(ParserSym.MINUS);
            }
          case 76: break;
          case 23: 
            { return symbol(ParserSym.LT);
            }
          case 77: break;
          case 24: 
            { return symbol(ParserSym.GETS);
            }
          case 78: break;
          case 25: 
            { return symbol(ParserSym.AND);
            }
          case 79: break;
          case 26: 
            { return symbol(ParserSym.OR);
            }
          case 80: break;
          case 27: 
            { string.append( yytext() );
            }
          case 81: break;
          case 28: 
            { string.append('\\');
            }
          case 82: break;
          case 29: 
            { yybegin(YYINITIAL);
                                     return new Symbol(ParserSym.STRING_LITERAL, stringStartRow, stringStartCol,
                                     string.toString());
            }
          case 83: break;
          case 30: 
            { return symbol(ParserSym.IF);
            }
          case 84: break;
          case 31: 
            { return symbol(ParserSym.error, "Invalid character constant");
            }
          case 85: break;
          case 32: 
            { return symbol(ParserSym.NOT_EQUAL);
            }
          case 86: break;
          case 33: 
            { return symbol(ParserSym.GEQ);
            }
          case 87: break;
          case 34: 
            { return symbol(ParserSym.LEQ);
            }
          case 88: break;
          case 35: 
            { return symbol(ParserSym.EQUAL);
            }
          case 89: break;
          case 36: 
            { string.append("\\r");
            }
          case 90: break;
          case 37: 
            { string.append("\\t");
            }
          case 91: break;
          case 38: 
            { string.append("\\n");
            }
          case 92: break;
          case 39: 
            { string.append('\"');
            }
          case 93: break;
          case 40: 
            { return symbol(ParserSym.USE);
            }
          case 94: break;
          case 41: 
            { return symbol(ParserSym.INT);
            }
          case 95: break;
          case 42: 
            { return symbol(ParserSym.CHARACTER_LITERAL, yytext().charAt(1));
            }
          case 96: break;
          case 43: 
            { return symbol(ParserSym.HIGH_MULT);
            }
          case 97: break;
          case 44: 
            { return symbol(ParserSym.ELSE);
            }
          case 98: break;
          case 45: 
            { return symbol(ParserSym.TRUE);
            }
          case 99: break;
          case 46: 
            { return symbol(ParserSym.BOOL);
            }
          case 100: break;
          case 47: 
            { return symbol(ParserSym.CHARACTER_LITERAL, '\r');
            }
          case 101: break;
          case 48: 
            { return symbol(ParserSym.CHARACTER_LITERAL, '\n');
            }
          case 102: break;
          case 49: 
            { string.append(hexToString(yytext()));
            }
          case 103: break;
          case 50: 
            { return symbol(ParserSym.FALSE);
            }
          case 104: break;
          case 51: 
            { return symbol(ParserSym.WHILE);
            }
          case 105: break;
          case 52: 
            { return symbol(ParserSym.LENGTH);
            }
          case 106: break;
          case 53: 
            { return symbol(ParserSym.RETURN);
            }
          case 107: break;
          case 54: 
            { return symbol(ParserSym.CHARACTER_LITERAL, hexToString(yytext().substring(1, yytext().length()-1)));
            }
          case 108: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
