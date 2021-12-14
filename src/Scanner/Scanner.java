/* The following code was generated by JFlex 1.2.2 on 11/18/20, 10:19 PM */

package Scanner;

import Parser.*;
import Utilities.*;
import Instruction.*;

/**
 * This class is a scanner generated by 
 * <a href="http://www.informatik.tu-muenchen.de/~kleing/jflex/">JFlex</a> 1.2.2
 * on 11/18/20, 10:19 PM from the specification file
 * <tt>file:/home/shyann/Documents/Fa20/326/Phase4/src/Scanner/EVM.flex</tt>
 */
public class Scanner implements java_cup.runtime.Scanner {

  /** this character denotes the end of file */
  final public static int YYEOF = -1;

  /** lexical states */
  final public static int YYINITIAL = 0;

  /** 
   * Translates characters to character classes
   */
  final private static char [] yycmap = {
     8,  8,  8,  8,  8,  8,  8,  8,  8,  3,  2,  0,  3,  1,  8,  8, 
     8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  0,  0,  0,  0, 
     3,  0, 18,  0,  6,  0,  0,  0,  8,  8,  0, 17,  0, 10,  7,  8, 
     9, 11, 11, 11, 11, 11, 11, 11, 11, 11, 20,  4,  6, 19,  6,  0, 
     0, 14, 14, 14, 14, 16, 15,  6,  6,  6,  6,  6, 12,  6,  6,  6, 
     6,  6,  6,  6,  6,  6,  6,  6, 13,  6,  6,  6,  5,  0,  0,  6, 
     0, 35, 14, 30, 32, 33, 34,  6, 31, 28,  6, 23, 21,  6,  6, 22, 
    25,  6,  6, 26, 29, 24,  6, 27, 13,  6,  6,  0,  0,  0,  0,  8
  };

  /** 
   * Translates a state to a row index in the transition table
   */
  final private static int yy_rowMap [] = { 
        0,    36,    72,    36,    36,   108,   144,   180,   216,   252, 
      288,   324,    36,    36,   360,   396,   108,   432,   468,   504, 
      540,    36,   576,    36,   612,   648,   324,   684,    36,    36, 
      720,   756,   144,   792,   828,   864,   900,   936,   972,  1008, 
     1044,  1080,  1116,  1152,  1188,  1224,  1260,  1296,  1332,  1368, 
     1404,  1440,   144,  1476,  1512,  1548,  1584,  1620,  1656,  1692, 
     1728,  1764,  1800,  1836,  1872,  1908,  1944,  1980
  };

  /** 
   * The packed transition table of the DFA
   */
  final private static String yy_packed = 
    "\1\2\1\3\1\4\1\5\1\6\1\2\1\7\1\10"+
    "\1\2\1\11\1\12\1\13\5\7\1\2\1\14\1\15"+
    "\1\16\1\17\12\7\1\20\3\7\46\0\1\4\41\0"+
    "\1\21\1\22\1\5\41\21\4\0\1\7\1\0\1\7"+
    "\1\0\2\7\1\0\6\7\4\0\17\7\4\0\1\7"+
    "\1\0\1\7\1\0\1\7\1\23\1\0\1\23\5\7"+
    "\4\0\17\7\7\0\1\24\1\0\1\25\1\0\1\25"+
    "\1\26\1\27\1\0\1\30\1\31\4\0\1\26\13\0"+
    "\1\31\1\30\14\0\1\32\37\0\1\24\1\0\1\13"+
    "\1\0\1\13\1\26\2\0\1\30\1\31\4\0\1\26"+
    "\13\0\1\31\1\30\1\0\1\33\1\34\1\35\2\33"+
    "\1\0\14\33\1\36\21\33\4\0\1\7\1\0\1\7"+
    "\1\0\2\7\1\0\6\7\4\0\1\7\1\37\15\7"+
    "\4\0\1\7\1\0\1\7\1\0\2\7\1\0\6\7"+
    "\4\0\14\7\1\40\2\7\2\0\1\5\45\0\1\7"+
    "\1\0\1\7\1\0\1\7\1\23\1\0\1\23\3\7"+
    "\1\41\1\42\4\0\14\7\1\42\1\41\1\7\11\0"+
    "\1\24\1\0\1\24\3\0\1\30\1\31\20\0\1\31"+
    "\1\30\10\0\1\24\1\0\1\25\1\0\1\25\3\0"+
    "\1\30\1\31\20\0\1\31\1\30\12\0\1\43\1\0"+
    "\1\44\2\0\3\44\15\0\1\44\1\0\4\44\11\0"+
    "\1\45\1\46\1\45\5\0\1\46\33\0\1\32\1\0"+
    "\1\32\1\26\10\0\1\26\20\0\1\35\45\0\1\7"+
    "\1\0\1\7\1\0\2\7\1\0\6\7\4\0\1\7"+
    "\1\47\15\7\4\0\1\7\1\0\1\7\1\0\2\7"+
    "\1\0\6\7\4\0\15\7\1\50\1\7\4\0\1\7"+
    "\1\0\1\7\1\0\1\7\1\51\1\46\1\51\5\7"+
    "\1\46\3\0\17\7\11\0\1\43\1\0\1\44\1\26"+
    "\1\0\3\44\4\0\1\26\10\0\1\44\1\0\4\44"+
    "\11\0\1\52\1\0\1\52\1\26\1\0\3\52\4\0"+
    "\1\26\10\0\1\52\1\0\4\52\11\0\1\45\1\0"+
    "\1\45\3\0\1\30\22\0\1\30\12\0\1\45\1\0"+
    "\1\45\34\0\1\7\1\0\1\7\1\0\2\7\1\0"+
    "\6\7\4\0\2\7\1\53\14\7\4\0\1\7\1\0"+
    "\1\7\1\0\2\7\1\0\6\7\4\0\16\7\1\54"+
    "\4\0\1\7\1\0\1\7\1\0\1\7\1\51\1\0"+
    "\1\51\3\7\1\41\1\7\4\0\15\7\1\41\1\7"+
    "\11\0\1\55\1\0\1\55\1\26\1\0\3\55\4\0"+
    "\1\26\10\0\1\55\1\0\4\55\4\0\1\7\1\0"+
    "\1\7\1\0\2\7\1\0\6\7\4\0\3\7\1\56"+
    "\13\7\4\0\1\7\1\0\1\7\1\0\2\7\1\0"+
    "\6\7\4\0\3\7\1\57\13\7\11\0\1\60\1\0"+
    "\1\60\1\26\1\0\3\60\4\0\1\26\10\0\1\60"+
    "\1\0\4\60\4\0\1\7\1\0\1\7\1\0\2\7"+
    "\1\0\6\7\4\0\4\7\1\61\12\7\4\0\1\7"+
    "\1\0\1\7\1\0\2\7\1\0\6\7\4\0\1\62"+
    "\16\7\11\0\1\63\1\0\1\63\1\26\1\0\3\63"+
    "\4\0\1\26\10\0\1\63\1\0\4\63\4\0\1\7"+
    "\1\0\1\7\1\0\2\7\1\0\6\7\4\0\5\7"+
    "\1\64\11\7\4\0\1\7\1\0\1\7\1\0\2\7"+
    "\1\0\6\7\4\0\10\7\1\65\6\7\11\0\1\66"+
    "\1\0\1\66\1\26\1\0\3\66\4\0\1\26\10\0"+
    "\1\66\1\0\4\66\4\0\1\7\1\0\1\7\1\0"+
    "\2\7\1\0\6\7\4\0\6\7\1\67\10\7\11\0"+
    "\1\70\1\0\1\70\1\26\1\0\3\70\4\0\1\26"+
    "\10\0\1\70\1\0\4\70\4\0\1\7\1\0\1\7"+
    "\1\0\2\7\1\0\6\7\4\0\7\7\1\71\7\7"+
    "\11\0\1\72\1\0\1\72\1\26\1\0\3\72\4\0"+
    "\1\26\10\0\1\72\1\0\4\72\4\0\1\7\1\0"+
    "\1\7\1\0\2\7\1\0\6\7\4\0\10\7\1\73"+
    "\6\7\11\0\1\74\1\0\1\74\1\26\1\0\3\74"+
    "\4\0\1\26\10\0\1\74\1\0\4\74\4\0\1\7"+
    "\1\0\1\7\1\0\2\7\1\0\6\7\4\0\11\7"+
    "\1\75\5\7\11\0\1\76\1\0\1\76\1\26\1\0"+
    "\3\76\4\0\1\26\10\0\1\76\1\0\4\76\4\0"+
    "\1\7\1\0\1\7\1\0\2\7\1\0\6\7\4\0"+
    "\12\7\1\65\4\7\11\0\1\77\1\0\1\77\1\26"+
    "\1\0\3\77\4\0\1\26\10\0\1\77\1\0\4\77"+
    "\11\0\1\100\1\0\1\100\1\26\1\0\3\100\4\0"+
    "\1\26\10\0\1\100\1\0\4\100\11\0\1\101\1\0"+
    "\1\101\1\26\1\0\3\101\4\0\1\26\10\0\1\101"+
    "\1\0\4\101\11\0\1\102\1\0\1\102\1\26\1\0"+
    "\3\102\4\0\1\26\10\0\1\102\1\0\4\102\11\0"+
    "\1\103\1\0\1\103\1\26\1\0\3\103\4\0\1\26"+
    "\10\0\1\103\1\0\4\103\11\0\1\104\1\0\1\104"+
    "\1\26\1\0\3\104\4\0\1\26\10\0\1\104\1\0"+
    "\4\104\14\0\1\26\10\0\1\26\16\0";

  /** 
   * The transition table of the DFA
   */
  final private static int yytrans [] = yy_unpack(yy_packed);


  /* error codes */
  final private static int YY_UNKNOWN_ERROR = 0;
  final private static int YY_ILLEGAL_STATE = 1;
  final private static int YY_NO_MATCH = 2;
  final private static int YY_PUSHBACK_2BIG = 3;

  /* error messages for the codes above */
  final private static String YY_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Internal error: unknown state",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * YY_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private final static byte YY_ATTRIBUTE[] = {
     0,  9,  1,  9,  9,  1,  1,  1,  1,  1,  1,  1,  9,  9,  1,  1, 
     0,  1,  1,  1,  1,  9,  0,  9,  0,  1,  0,  1,  9,  9,  1,  1, 
     1,  1,  1,  1,  1,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  1,  0,  0,  0, 
     0,  0,  0,  0
  };

  /** the input device */
  private java.io.Reader yy_reader;

  /** the current state of the DFA */
  private int yy_state;

  /** the current lexical state */
  private int yy_lexical_state = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char yy_buffer[] = new char[16384];

  /** the textposition at the last accepting state */
  private int yy_markedPos;

  /** the textposition at the last state to be included in yytext */
  private int yy_pushbackPos;

  /** the current text position in the buffer */
  private int yy_currentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int yy_startRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int yy_endRead;

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
   * yy_atBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean yy_atBOL;

  /** yy_atEOF == true <=> the scanner has returned a value for EOF */
  private boolean yy_atEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean yy_eof_done;

  /* user code: */
  private java_cup.runtime.Symbol token(int kind, String str) {
    return new java_cup.runtime.Symbol(kind, str);
  }

  private java_cup.runtime.Symbol token(int kind, Integer val) {
    return new java_cup.runtime.Symbol(kind, val);
  }

  private java_cup.runtime.Symbol token(int kind, Number val) {
    return new java_cup.runtime.Symbol(kind, val);
  }

  private java_cup.runtime.Symbol token(int kind) {
    return new java_cup.runtime.Symbol(kind, null);
  }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Scanner(java.io.Reader in) {
    this.yy_reader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Scanner(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed DFA transition table.
   *
   * @param packed   the packed transition table
   * @return         the unpacked transition table
   */
  private static int [] yy_unpack(String packed) {
    int [] trans = new int[2016];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1228) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do trans[j++] = value; while (--count > 0);
    }
    return trans;
  }


  /**
   * Gets the next input character.
   *
   * @return      the next character of the input stream, EOF if the
   *              end of the stream is reached.
   * @exception   IOException  if any I/O-Error occurs
   */
  private int yy_advance() throws java.io.IOException {

    /* standard case */
    if (yy_currentPos < yy_endRead) return yy_buffer[yy_currentPos++];

    /* if the eof is reached, we don't need to work hard */ 
    if (yy_atEOF) return YYEOF;

    /* otherwise: need to refill the buffer */

    /* first: make room (if you can) */
    if (yy_startRead > 0) {
      System.arraycopy(yy_buffer, yy_startRead, 
                       yy_buffer, 0, 
                       yy_endRead-yy_startRead);

      /* translate stored positions */
      yy_endRead-= yy_startRead;
      yy_currentPos-= yy_startRead;
      yy_markedPos-= yy_startRead;
      yy_pushbackPos-= yy_startRead;
      yy_startRead = 0;
    }

    /* is the buffer big enough? */
    if (yy_currentPos >= yy_buffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[yy_currentPos*2];
      System.arraycopy(yy_buffer, 0, newBuffer, 0, yy_buffer.length);
      yy_buffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = yy_reader.read(yy_buffer, yy_endRead, 
                                            yy_buffer.length-yy_endRead);

    if ( numRead == -1 ) return YYEOF;

    yy_endRead+= numRead;

    return yy_buffer[yy_currentPos++];
  }


  /**
   * Closes the input stream.
   */
  final public void yyclose() throws java.io.IOException {
    yy_atEOF = true;            /* indicate end of file */
    yy_endRead = yy_startRead;  /* invalidate buffer    */
    yy_reader.close();
  }


  /**
   * Returns the current lexical state.
   */
  final public int yystate() {
    return yy_lexical_state;
  }

  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  final public void yybegin(int newState) {
    yy_lexical_state = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  final public String yytext() {
    return new String( yy_buffer, yy_startRead, yy_markedPos-yy_startRead );
  }

  /**
   * Returns the length of the matched text region.
   */
  final public int yylength() {
    return yy_markedPos-yy_startRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void yy_ScanError(int errorCode) {
    try {
      System.out.println(YY_ERROR_MSG[errorCode]);
    }
    catch (ArrayIndexOutOfBoundsException e) {
      System.out.println(YY_ERROR_MSG[YY_UNKNOWN_ERROR]);
    }

    System.exit(1);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  private void yypushback(int number) {
    if ( number > yylength() )
      yy_ScanError(YY_PUSHBACK_2BIG);

    yy_markedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void yy_do_eof() throws java.io.IOException {
    if (!yy_eof_done) {
      yy_eof_done = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int yy_input;
    int yy_action;


    while (true) {

      boolean yy_counted = false;
      for (yy_currentPos = yy_startRead; yy_currentPos < yy_markedPos;
                                                      yy_currentPos++) {
        switch (yy_buffer[yy_currentPos]) {
        case '\r':
          yyline++;
          yycolumn = 0;
          yy_counted = true;
          break;
        case '\n':
          if (yy_counted)
            yy_counted = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          yy_counted = false;
          yycolumn++;
        }
      }

      if (yy_counted) {
        if ( yy_advance() == '\n' ) yyline--;
        if ( !yy_atEOF ) yy_currentPos--;
      }

      yy_action = -1;

      yy_currentPos = yy_startRead = yy_markedPos;

      yy_state = yy_lexical_state;


      yy_forAction: {
        while (true) {

          yy_input = yy_advance();

          if ( yy_input == YYEOF ) break yy_forAction;

          int yy_next = yytrans[ yy_rowMap[yy_state] + yycmap[yy_input] ];
          if (yy_next == -1) break yy_forAction;
          yy_state = yy_next;

          int yy_attributes = YY_ATTRIBUTE[yy_state];
          if ( (yy_attributes & 1) > 0 ) {
            yy_action = yy_state; 
            yy_markedPos = yy_currentPos; 
            if ( (yy_attributes & 8) > 0 ) break yy_forAction;
          }

        }
      }


      switch (yy_action) {    

        case 27: 
        case 28: 
          {  throw new RuntimeException("Unterminated string at end-of-line \""+yytext()+"\" at line "+(yyline+1)+", column "+(yycolumn+1)); }
        case 69: break;
        case 52: 
          {  return token(ReservedWords.get(yytext()));  }
        case 70: break;
        case 34: 
        case 35: 
        case 41: 
        case 44: 
        case 47: 
        case 50: 
        case 53: 
        case 55: 
        case 57: 
          {  Number num = ScannerUtils.convertNumber(yytext());
				  if (num instanceof Integer)
				    return token(sym.Int, num);
				  else
				    return token(sym.Num, num);
				 }
        case 71: break;
        case 29: 
          {  return token(sym.Str, yytext().substring(1,yytext().length()-1));  }
        case 72: break;
        case 21: 
          {  return token(sym.Num, ScannerUtils.convertNumber(yytext().substring(0,yytext().length()-1)));  }
        case 73: break;
        case 18: 
        case 19: 
        case 20: 
        case 23: 
        case 32: 
        case 36: 
        case 40: 
          {  return token(sym.Num, ScannerUtils.convertNumber(yytext()));  }
        case 74: break;
        case 1: 
        case 5: 
        case 9: 
        case 11: 
          {  throw new RuntimeException("Illegal character \""+yytext()+"\" at line "+(yyline+1)+", column "+(yycolumn+1));  }
        case 75: break;
        case 2: 
        case 3: 
          {  return token(sym.SEP);  }
        case 76: break;
        case 6: 
        case 7: 
        case 14: 
        case 15: 
        case 30: 
        case 31: 
        case 33: 
        case 38: 
        case 39: 
        case 42: 
        case 43: 
        case 45: 
        case 46: 
        case 48: 
        case 49: 
        case 51: 
        case 54: 
        case 56: 
        case 58: 
        case 60: 
          {  if (InstInfo.contains(yytext()))
				     return token(sym.Insn, yytext());
				   else if (ReservedWords.contains(yytext()))
				     return token(ReservedWords.get(yytext()));
	                           else				
				     return token(sym.Word, yytext());
				  }
        case 77: break;
        case 8: 
        case 10: 
        case 25: 
          {  Number num = ScannerUtils.convertNumber(yytext());
				   if (num instanceof Integer) 
			             return token(sym.Int, num);
				   else 
                                     return token(sym.Num, num);
                                  }
        case 78: break;
        case 12: 
          {  return token(sym.EQ);  }
        case 79: break;
        case 13: 
          {  return token(sym.COLON);  }
        case 80: break;
        case 17: 
          {    }
        case 81: break;
        case 4: 
          {  }
        case 82: break;
        default: 
          if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
            yy_atEOF = true;
            yy_do_eof();
              { return new java_cup.runtime.Symbol(sym.EOF); }
          } 
          else {
            yy_ScanError(YY_NO_MATCH);
          }
      }
    }
  }    


}
