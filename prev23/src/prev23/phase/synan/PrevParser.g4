parser grammar PrevParser;

@header {

	package prev23.phase.synan;
	
	import java.util.*;
	
	import prev23.common.report.*;
	import prev23.phase.lexan.*;
	
}

options{
    tokenVocab=PrevLexer;
}

source : decl ;

// Declarations
decl : some_decl decl | ;
some_decl : type_decl | fun_decl | var_decl ;

// Type declarations
type_decl : TYP type_decl1 SEMI ;
type_decl1 : type_ass type_decl2 ;
type_decl2 : COMMA type_decl1 | ;
type_ass : ID ASS type ;

// Function declarations
fun_decl : FUN fun_decl1 SEMI ;
fun_decl1 : fun_def fun_decl2 ;
fun_decl2 : fun_def fun_decl2 | ;

fun_def : ID LPAR fun_args RPAR COLON fun_body ;
fun_args : fun_args1 | ;
fun_args1 : fun_args_def fun_args2 ;
fun_args2 : COMMA fun_args_def fun_args2 | ;
fun_args_def : ID COLON type ;
fun_body : type fun_body1 ;
fun_body1 : ASS statement | ;

// Variable declarations
var_decl : VAR var_decl1 SEMI ;
var_decl1 : var_def var_decl2 ;
var_decl2 : COMMA var_def var_decl2 | ;
var_def : ID COLON type ;

// Type
type : raw_type | expr_type | HAT type | struct_type | LPAR type RPAR ;
raw_type : VOID | CHAR | INT | BOOL | ID ;
expr_type : LBRACK expr RBRACK type ;
struct_type : LCURLY struct_types RCURLY ;
struct_types : struct_type_def struct_types1 ;
struct_types1 : COMMA struct_type_def struct_types1 | ;
struct_type_def : ID COLON type;

// Expression
expr : disjunctive_expr ;

disjunctive_expr : conjunctive_expr disjunctive_expr1 ;
disjunctive_expr1 : disjunctive_op conjunctive_expr disjunctive_expr1 | ;

conjunctive_expr : relational_expr conjunctive_expr1 ;
conjunctive_expr1 : conjunctive_op relational_expr conjunctive_expr1 | ;

relational_expr : additive_expr relational_expr1 ;
relational_expr1 : relational_op additive_expr relational_expr1 | ;

additive_expr : multiplicative_expr additive_expr1 ; 
additive_expr1 : additive_op multiplicative_expr  additive_expr1 | ;

multiplicative_expr : prefix_expr multiplicative_expr1 ;
multiplicative_expr1 : multiplicative_op prefix_expr multiplicative_expr1 | ;

prefix_expr : prefix_expr1 postfix_expr ;
prefix_expr1 : prefix_op prefix_expr1 | ;

postfix_expr : primary_expr postfix_expr1 ;
postfix_expr1 : postfix_op postfix_expr1 | ;

primary_expr : const_expr | id_expr | mem_expr | cast_expr ;
const_expr : CONST_INT | CONST_CHAR | CONST_STR | NONE | TRUE | FALSE | NIL;
id_expr : ID id_expr1;
id_expr1 : LPAR id_expr2 RPAR | ;
id_expr2 : expr id_expr3 | ;
id_expr3 : COMMA expr id_expr3 | ;
mem_expr : NEW LPAR type RPAR | DEL LPAR expr RPAR; 
cast_expr : LPAR expr cast_expr1 RPAR ;
cast_expr1 : COLON type ;

// Statement
statement : assign_statement | if_statement | while_statement | let_statement | block_statement;

assign_statement : expr assign_statement1 ;
assign_statement1 : ASS expr | ;

if_statement : IF expr THEN if_statement1 ;
if_statement1 : statement | statement ELSE statement ;

while_statement : WHILE expr DO statement ;

let_statement : LET decl IN statement ;

block_statement : LCURLY statement block_statement1 RCURLY ;
block_statement1 : SEMI statement block_statement1 | ;

// Operators

disjunctive_op : OR;
conjunctive_op : AND;
relational_op : EQ | NEQ | LT | GT | LEQ | GEQ;
additive_op : PLUS | MINUS;
multiplicative_op : MUL | DIV | MOD;
prefix_op : NOT | PLUS | MINUS | HAT;
postfix_op : LBRACK expr RBRACK | HAT | DOT ID ;
