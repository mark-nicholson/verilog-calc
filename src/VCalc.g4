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
	|	INT						# int
	|	ID						# id
	|	'(' expr ')'			# parens
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

/* Commenting */
COMMENT			:	'/*' ( . )*? '*/'	->	skip ;
// OP_DIV_FLOOR as '//' conflicts with line-comment -- Because it is stripped early, it is gone before tokenizing
LINE_COMMENT	:   '//' ~('\n'|'\r')* '\r'? '\n'	->	skip ;

/* General Items */
ID	:	[a-zA-Z]+ ;
INT	:	[0-9]+ ;
NEWLINE	:	'\r'? '\n' ;
WS	:	[ \t]+	->	skip ;

