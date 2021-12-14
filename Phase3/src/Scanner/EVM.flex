package Scanner;

import Parser.*;
import Utilities.*;
import Instruction.*;
%%
%class Scanner
%public
%7bit
%pack

%cup

%line
%column



%{
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
%}  



/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace =  [ \t\f]

/* comments */
Comment = ";" {InputCharacter}* {LineTerminator}


/* String Literals */
StringCharacter = [^\r\n\"\\]

/* identifiers */
Identifier = ([:jletter:]|\.|<|>|\[)(\/|[:jletterdigit:]|\(|\)|<|>|;|\[)*

/* Integer Literals */
DecIntegerLiteral = 0 |[-]? [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]
 
HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]
 
/* Floating Point Literals */
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}|{FLit4}) [fF]
DoubleLiteral = {FLit1}|{FLit2}|{FLit3}|{FLit4} 
 
FLit1 = [0-9]+ \. [0-9]* {Exponent}?
FLit2 = \. [0-9]+ {Exponent}?
FLit3 = [0-9]+ {Exponent}
FLit4 = [0-9]+ {Exponent}?
 
Exponent = [eE] [+\-]? [0-9]+       

%%

  \"({StringCharacter})*\"               { return token(sym.Str, yytext().substring(1,yytext().length()-1)); }
  \"({StringCharacter})*{LineTerminator} { throw new RuntimeException("Unterminated string at end-of-line \""+yytext()+"\" at line "+(yyline+1)+", column "+(yycolumn+1));}


  /* numeric literals */
  {DecIntegerLiteral}            { Number num = ScannerUtils.convertNumber(yytext());
				   if (num instanceof Integer) 
			             return token(sym.Int, num);
				   else 
                                     return token(sym.Num, num);
                                 } 
  {DecLongLiteral}		{ return token(sym.Num, ScannerUtils.convertNumber(yytext().substring(0,yytext().length()-1))); }
  {HexIntegerLiteral}		{ Number num = ScannerUtils.convertNumber(yytext());
				  if (num instanceof Integer)
				    return token(sym.Int, num);
				  else
				    return token(sym.Num, num);
				}	
  {HexLongLiteral}             	{ return token(sym.Num, ScannerUtils.convertNumber(yytext().substring(0,yytext().length()-1))); }
  {FloatLiteral}	 	{ return token(sym.Num, ScannerUtils.convertNumber(yytext())); }
  {DoubleLiteral}		{ return token(sym.Num, ScannerUtils.convertNumber(yytext())); }
  

  "="				{ return token(sym.EQ); }
  ":"                           { return token(sym.COLON); }


  /* comments */
  {Comment}                      {  }
  /* whitespace */
  {WhiteSpace}                   {}

  /* identifiers */ 
  "lookupswitch"                 { return token(ReservedWords.get(yytext())); }
  "default"                      { return token(ReservedWords.get(yytext())); }
  {Identifier}                   { if (InstInfo.contains(yytext()))
				     return token(sym.Insn, yytext());
				   else if (ReservedWords.contains(yytext()))
				     return token(ReservedWords.get(yytext()));
	                           else				
				     return token(sym.Word, yytext());
				 }


  {LineTerminator}              { return token(sym.SEP); }
				     

/* error fallback */
.|\n                             { throw new RuntimeException("Illegal character \""+yytext()+"\" at line "+(yyline+1)+", column "+(yycolumn+1)); }
