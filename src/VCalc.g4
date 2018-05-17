/**
 * Define a grammar called Hello
 */

grammar VCalc;
 
@header {
	package vcalc;
}

prog:	stat+ ;

stat:	expr NEWLINE			# printExpr
	|	ID '=' expr NEWLINE		# assign
	|	NEWLINE					# blank
	;
	
expr:	expr op=('*'|'/') expr	# MulDiv
	|	expr op=('+'|'-') expr	# AddSub
	|	INT						# int
	|	ID						# id
	|	'(' expr ')'			# parens
	;
 
ID	:	[a-zA-Z]+ ;
INT	:	[0-9]+ ;
NEWLINE	:	'\r'? '\n' ;
WS	:	[ \t]+	->	skip ;

/* names for Tokens */
MUL	:	'*'	;
DIV	:	'/'	;
ADD	:	'+'	;
SUB	:	'-'	;
