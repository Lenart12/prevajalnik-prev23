lexer grammar PrevLexer;

@header {
	package prev23.phase.lexan;
	import prev23.common.report.*;
	import prev23.data.sym.*;
}

@members {
    @Override
	public Token nextToken() {
		return (Token) super.nextToken();
	}

	private void LexError(String message) {
		throw new Report.Error(
			new Location(
				_tokenStartLine, _tokenStartCharPositionInLine + 1,
				getLine(), getCharPositionInLine() + 1),
			String.format("%s [%s]", message, getText())
		);
	}

	public void OnTab() {
		var tab_start = _tokenStartCharPositionInLine;
		var tab_end = tab_start + 8 - (tab_start % 8);
		setCharPositionInLine(tab_end);
	}
}

// Symbols
LPAR  : '(' ;
RPAR  : ')' ;
LCURLY: '{' ;
RCURLY: '}' ;
LBRACK: '[' ;
RBRACK: ']' ;
DOT   : '.' ;
COMMA : ',' ;
COLON : ':' ;
SEMI  : ';' ;
AND   : '&' ;
OR    : '|' ;
NOT   : '!' ;
EQ    : '==' ;
NEQ   : '!=' ;
LT    : '<' ;
GT    : '>' ;
LEQ   : '<=' ;
GEQ   : '>=' ;
MUL   : '*' ;
DIV   : '/' ;
MOD   : '%' ;
PLUS  : '+' ;
MINUS : '-' ;
HAT   : '^' ;
ASS   : '=' ;

// Keywords
BOOL : 'bool' ;
CHAR : 'char' ;
DEL  : 'del' ;
DO   : 'do' ;
ELSE : 'else' ;
FUN  : 'fun' ;
IF   : 'if' ;
IN   : 'in' ;
INT  : 'int' ;
LET  : 'let' ;
NEW  : 'new' ;
THEN : 'then' ;
TYP  : 'typ' ;
VAR  : 'var' ;
VOID : 'void' ;
WHILE: 'while' ;

// Constant
NONE : 'none' ;
TRUE : 'true' ;
FALSE: 'false' ;
NIL  : 'nil' ;
CONST_INT
	: ([1-9]+ [0-9]*)
	| ([0] [0-9]+ { LexError("Zero padded int not allowed"); })
	| [0];
CONST_CHAR:
	'\''
	(
		'\\\'' |
		[\u0020-\u0026] |
		[\u0028-\u007E] |
		('\'' { LexError("Empty char constant"); }) |
		. '\'' { LexError(String.format("Unexpected symbol [%c] inside char constant", _input.LA(-2))); } |
		.
	)
	(
		'\'' |
		.*? ('\'' | '\n' | EOF) { LexError("Invalid char constant"); }
	) ;
CONST_STR :
	'"'
	(
		'\\"' |
		[\u0020-\u0021] |
		[\u0023-\u007E]
	)*
	(
		'"' |
		('\n' | EOF ) { LexError("Expected end of string"); } |
		. { LexError(String.format("Unexpected symbol [%c] inside string constant", _input.LA(-1))); }
	) ;

ID : [a-zA-Z_]+[a-zA-Z0-9_]* ;

// Ignored
WS     : [ \r\n] -> skip ;
TAB : [\t] { OnTab(); } -> skip ;
COMMENT: '#' .*? ('\n' | EOF) -> skip ;

ERROR  : . { LexError("Undefined symbol or token"); } -> skip;