/**
 * Define a grammar called Hello
 */

grammar VCalc;
 
@header {
	package vcalc;
}

prog:	stat+ ;

stat:	expr NEWLINE					# printExpr
	|	ID OP_ASSIGN expr NEWLINE		# assign
	|	NEWLINE							# blank
	;
	
expr:	<assoc=right> expr OP_POW  expr		# Exponent
	|	OP_BIT_INVERT expr					# BitInvert
	|	op=(OP_PLUS | OP_MINUS) expr		# UnarySign			
	|	expr op=(
						OP_MULT
					|	OP_DIV_FLOOR
					|	OP_MODULUS
					|	OP_DIV
				) expr						# MulDivMod
	|	expr op=(OP_PLUS|OP_MINUS) expr		# AddSub
	|	expr op=(
						OP_UP_SHIFT
					|	OP_UP_SHIFT_EXTEND
					|	OP_DOWN_SHIFT
					|	OP_DOWN_SHIFT_EXTEND
				) expr						# BitShift
	|	expr OP_AND expr					# BitAnd
	|	expr OP_XOR expr					# BitXor
	|	expr OP_OR expr						# BitOr
	|	(numeric_literal|verilog_literal)	# NumericLiteral
	|	ID						# id
	|	'(' expr ')'			# parens
	;

numeric_literal:
	value=(
			DECIMAL_LITERAL
		|	OCTAL_LITERAL
		|	BIN_LITERAL
		|	HEX_LITERAL
	)							# int
	;

/* names for Tokens */
OP_PLUS:                  '+' ;
OP_MINUS:                 '-' ;  
OP_MULT:                  '*' ;
OP_DIV:                   '/' ;
OP_DIV_FLOOR:             '/_' ;
OP_MODULUS:               '%';
OP_UP_SHIFT:              '<<' ; 
OP_DOWN_SHIFT:            '>>' ; 
OP_UP_SHIFT_EXTEND:       '<<<' ;  
OP_DOWN_SHIFT_EXTEND:     '>>>' ;  
OP_POW:                   '**' ; 
OP_AND:                   '&' ;  
OP_OR:                    '|' ;  
OP_XOR:                   '^' ;  
OP_BIT_INVERT:            '~' ;  
OP_ASSIGN:                '=' ;

COMMA:						',' ;
OPEN_BRACE:					'{' ;
CLOSE_BRACE:				'}' ;

/* Commenting */
COMMENT			:	'/*' ( . )*? '*/'	->	skip ;
// OP_DIV_FLOOR as '//' conflicts with line-comment -- Because it is stripped early, it is gone before tokenizing
LINE_COMMENT	:   '//' ~('\n'|'\r')* '\r'? '\n'	->	skip ;

/* General Items */
ID	:	[a-zA-Z]+ ;
NEWLINE	:	'\r'? '\n' ;
WS	:	[ \t]+	->	skip ;

/* Literals */
OCTAL_LITERAL		:	('0' NonZeroOctDigit OctDigit* ) ;
DECIMAL_LITERAL		:	('0' | NonZeroDecDigit DecDigit*) ;
HEX_LITERAL			:	('0' ('x'|'X') HexDigit+) ;
BIN_LITERAL			:	('0' ('b'|'B') BinDigit+) ;

/* building blocks */
fragment HexDigit			: [0-9a-fA-F] ;
fragment NonZeroDecDigit	: [1-9] ;
fragment DecDigit			: ('0' | NonZeroDecDigit) ;
fragment NonZeroOctDigit	: [1-7] ;
fragment OctDigit			: ('0' | NonZeroOctDigit) ;
fragment BinDigit			: [0-1] ;


/*******************************************************************************
 *
 *                 Verilog Numeric Support
 * 
 * http://www.asic-world.com/verilog/operators2.html
 * http://www.verilog.com/VerilogBNF.html#REF148
 *
 *******************************************************************************/

verilog_literal:
	value=(
			VERILOG_BIN_LITERAL
		|	VERILOG_OCT_LITERAL
		|	VERILOG_DEC_LITERAL
		|	VERILOG_HEX_LITERAL
	)												# Verilog
      /* Support aggregated values */
	| repeat=DECIMAL_LITERAL? OPEN_BRACE vn=verilog_literal ( COMMA vn=verilog_literal )* CLOSE_BRACE		# VerilogAggregate
	;


/* base verilog numbers */
VERILOG_BIN_LITERAL:  NonZeroDecDigit+ VERILOG_BASE_BINARY      VerilogBinDigit+ ;
VERILOG_DEC_LITERAL:  NonZeroDecDigit+ VERILOG_BASE_DECIMAL     DecDigit+ ;
VERILOG_HEX_LITERAL:  NonZeroDecDigit+ VERILOG_BASE_HEXADECIMAL VerilogHexDigit+ ;
VERILOG_OCT_LITERAL:  NonZeroDecDigit+ VERILOG_BASE_OCTAL       VerilogOctDigit+ ;


/* meta states */
fragment Spacers  : ('_') ;
fragment TriState : ('z' | 'Z') ;
fragment DontCare : ('x' | 'X') ;

/* verilog digits */
fragment VerilogBinDigit : ( BinDigit | TriState | DontCare | Spacers ) ;
fragment VerilogDecDigit : ( DecDigit | TriState | DontCare | Spacers ) ;
fragment VerilogOctDigit : ( OctDigit | TriState | DontCare | Spacers ) ;
fragment VerilogHexDigit : ( HexDigit | TriState | DontCare | Spacers ) ;

/* VERILOG Base Identifiers */
fragment VERILOG_TICK:             '\'' ;
fragment VERILOG_BASE_BINARY:      VERILOG_TICK ( 'b' | 'B' ) ;
fragment VERILOG_BASE_DECIMAL:     VERILOG_TICK ( 'd' | 'D' ) ;
fragment VERILOG_BASE_OCTAL:       VERILOG_TICK ( 'o' | 'O' ) ;
fragment VERILOG_BASE_HEXADECIMAL: VERILOG_TICK ( 'h' | 'H' ) ;
