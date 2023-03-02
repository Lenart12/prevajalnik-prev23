parser grammar PrevParser;

@header {
    package prev23.phase.synan;
    import java.util.*;
    import prev23.common.report.*;
    import prev23.phase.lexan.*;
    import prev23.data.ast.tree.*;
    import prev23.data.ast.tree.decl.*;
    import prev23.data.ast.tree.expr.*;
    import prev23.data.ast.tree.stmt.*;
    import prev23.data.ast.tree.type.*;
}

@members {
    // New vector
    private <T> LinkedList<T> nv() {
        return new LinkedList<T>();
    }
    // Add to vector
    private <T> LinkedList<T> av(LinkedList<T> target, T one_item, LinkedList<T> more) {
        if (one_item != null) target.add(one_item);
        if (more != null) target.addAll(more);
        return target;
    }
    // Add to new vector
    private <T> LinkedList<T> cons(T one_item, LinkedList<T> more) {
        LinkedList<T> target = nv();
        return av(target, one_item, more);
    }
    private Location l(Token that) {
        return new Location((prev23.data.sym.Token)that);
    }
    private Location l(Locatable that) {
        return new Location(that);
    }
    private Location l(Locatable lhs, Locatable rhs) {
        return new Location(lhs, rhs);
    }
    private Location l(Token lhs, Token rhs) {
        return new Location((prev23.data.sym.Token)lhs, (prev23.data.sym.Token)rhs);
    }
    private Location l(Locatable lhs, Token rhs) {
        return new Location(lhs, (prev23.data.sym.Token)rhs);
    }
    private Location l(Token lhs, Locatable rhs) {
        return new Location((prev23.data.sym.Token)lhs, rhs);
    }
    private Location l(Token lhs, Locatable rhs, Locatable ohs) {
        if (ohs != null ) return new Location((prev23.data.sym.Token)lhs, ohs);
        return new Location((prev23.data.sym.Token)lhs, rhs);
    }
}

options{
    tokenVocab=PrevLexer;
}

source
    returns [AstTrees<AstTrees<AstDecl>> ast] 
    : decl EOF {$ast = $decl.ast;}
    ;

// Declarations
decl
    returns [AstTrees<AstTrees<AstDecl>> ast]
    : some_decl decl1 { $ast = new AstTrees<AstTrees<AstDecl>>("declarations", cons($some_decl.ast, $decl1.ast)); }
    ;
decl1
    returns [LinkedList<AstTrees<AstDecl>> ast]
    : some_decl decl1 { $ast = cons($some_decl.ast, $decl1.ast); }
    | { $ast = null; }
    ;
some_decl
    returns [AstTrees<AstDecl> ast]
    : type_decl { $ast = (AstTrees<AstDecl>)(AstTrees<?>)$type_decl.ast; }
    | fun_decl { $ast = (AstTrees<AstDecl>)(AstTrees<?>)$fun_decl.ast; }
    | var_decl { $ast = (AstTrees<AstDecl>)(AstTrees<?>)$var_decl.ast; }
    ;

// Type declarations
type_decl
    returns [AstTrees<AstTypDecl> ast]
    : TYP type_decl1 SEMI {$ast = new AstTrees<AstTypDecl>(l($TYP, $SEMI), "type declarations", $type_decl1.ast); } 
    ;
type_decl1
    returns [LinkedList<AstTypDecl> ast]
    : type_ass type_decl2 { $ast = cons($type_ass.ast, $type_decl2.ast); }
    ;
type_decl2
    returns [LinkedList<AstTypDecl> ast]
    : COMMA type_ass type_decl2 { $ast = cons($type_ass.ast, $type_decl2.ast); }
    | { $ast = null; }
    ;
type_ass
    returns [AstTypDecl ast]
    : ID ASS type {$ast = new AstTypDecl(l($ID, $type.ast), $ID.text, $type.ast);}
    ;

// Function declarations
fun_decl
    returns [AstTrees<AstFunDecl> ast]
    : FUN fun_decl1 SEMI {$ast = new AstTrees<AstFunDecl>(l($FUN, $SEMI), "function declarations", $fun_decl1.ast); }
    ;
fun_decl1
    returns [LinkedList<AstFunDecl> ast]
    : fun_def fun_decl2 {$ast = cons($fun_def.ast, $fun_decl2.ast);}
    ;
fun_decl2
    returns [LinkedList<AstFunDecl> ast]
    : COMMA fun_def fun_decl2 {$ast = cons($fun_def.ast, $fun_decl2.ast);}
    | {$ast = null;}
    ;

fun_def
    returns [AstFunDecl ast]
    : ID LPAR fun_args RPAR COLON type fun_body
        {$ast = new AstFunDecl(l($ID, $type.ast, $fun_body.ast), $ID.text, $fun_args.ast, $type.ast, $fun_body.ast);}
    ;
fun_args
    returns [AstTrees<AstParDecl> ast]
    : fun_args1 {$ast = new AstTrees<AstParDecl>("function arguments", $fun_args1.ast); }
    | {$ast = new AstTrees<AstParDecl>("function arguments"); }
    ;
fun_args1
    returns [LinkedList<AstParDecl> ast]
    : fun_args_def fun_args2 {$ast = cons($fun_args_def.ast, $fun_args2.ast);}
    ;
fun_args2
    returns [LinkedList<AstParDecl> ast]
    : COMMA fun_args_def fun_args2 {$ast = cons($fun_args_def.ast, $fun_args2.ast);}
    | {$ast = null;}
    ;
fun_args_def
    returns [AstParDecl ast]
    : ID COLON type {$ast = new AstParDecl(l($ID, $type.ast), $ID.text, $type.ast); }
    ;
fun_body
    returns [AstStmt ast]
    : ASS statement { $ast = $statement.ast; }
    | { $ast = null; }
    ;

// Variable declarations
var_decl
    returns [AstTrees<AstVarDecl> ast]
    : VAR var_decl1 SEMI  {$ast = new AstTrees<AstVarDecl>(l($VAR, $SEMI), "variable declarations", $var_decl1.ast); }
    ;
var_decl1
    returns [LinkedList<AstVarDecl> ast]
    : var_def var_decl2 {$ast = cons($var_def.ast, $var_decl2.ast);}
    ;
var_decl2
    returns [LinkedList<AstVarDecl> ast]
    : COMMA var_def var_decl2 {$ast = cons($var_def.ast, $var_decl2.ast);}
    | { $ast = null; } 
    ;
var_def
    returns [AstVarDecl ast]
    : ID COLON type {$ast = new AstVarDecl(l($ID, $type.ast), $ID.text, $type.ast);}
    ;

// Type
type
    returns [AstType ast]
    : raw_type {$ast = $raw_type.ast;}
    | expr_type {$ast = $expr_type.ast;}
    | HAT type {$ast = new AstPtrType(l($HAT, $type.ast), $type.ast);}
    | record_type {$ast = $record_type.ast;} 
    | LPAR type RPAR {$ast = $type.ast;} 
    ;
raw_type
    returns [AstType ast]
    : VOID {$ast = new AstAtomType(l($VOID), AstAtomType.Type.VOID); }
    | CHAR {$ast = new AstAtomType(l($CHAR), AstAtomType.Type.CHAR); }
    | INT {$ast = new AstAtomType(l($INT), AstAtomType.Type.INT); }
    | BOOL {$ast = new AstAtomType(l($BOOL), AstAtomType.Type.BOOL); }
    | ID  {$ast = new AstNameType(l($ID), $ID.text); }
    ;
expr_type
    returns [AstArrType ast]
    : LBRACK expr RBRACK type {$ast = new AstArrType(l($LBRACK, $type.ast), $type.ast, $expr.ast);} 
    ;
record_type
    returns [AstRecType ast]
    : LCURLY record_types RCURLY {$ast = new AstRecType(l($LCURLY, $RCURLY), $record_types.ast);}
    ;
record_types
    returns [AstTrees<AstCmpDecl> ast]
    : record_type_def record_types1
        {$ast = new AstTrees<AstCmpDecl>("record declarations", cons($record_type_def.ast, $record_types1.ast));}
    ;
record_types1
    returns [LinkedList<AstCmpDecl> ast]
    : COMMA record_type_def record_types1 {$ast = cons($record_type_def.ast, $record_types1.ast);}
    | {$ast = null;}
    ;
record_type_def
    returns [AstCmpDecl ast]
    : ID COLON type {$ast = new AstCmpDecl(l($ID, $type.ast), $ID.text, $type.ast);}
    ;

// Expression
expr
    returns [AstExpr ast]
    : disjunctive_expr {$ast = $disjunctive_expr.ast;} 
    ;

disjunctive_expr
    returns [AstExpr ast]
    : left=conjunctive_expr disjunctive_expr1[$left.ast]
      {$ast = $disjunctive_expr1.ast;}
    ;
disjunctive_expr1
    [AstExpr left] returns [AstExpr ast]
    : op=disjunctive_op right=conjunctive_expr 
      {$left = new AstBinExpr(l($left, $right.ast), $op.op, $left, $right.ast);}
      disjunctive_expr1[$left]
      {$ast = $disjunctive_expr1.ast;}
    | {$ast = $left;}
    ;

conjunctive_expr
    returns [AstExpr ast]
    : left=relational_expr conjunctive_expr1[$left.ast]
      {$ast = $conjunctive_expr1.ast;}
    ;
conjunctive_expr1
    [AstExpr left] returns [AstExpr ast]
    : op=conjunctive_op right=relational_expr
      {$left = new AstBinExpr(l($left, $right.ast), $op.op, $left, $right.ast);}
      conjunctive_expr1[$left]
      {$ast = $conjunctive_expr1.ast;}
    | {$ast = $left;}
    ;

relational_expr
    returns [AstExpr ast]
    : left=additive_expr relational_expr1[$left.ast]
      {$ast = $relational_expr1.ast;}
    ;
relational_expr1
    [AstExpr left] returns [AstExpr ast]
    : op=relational_op right=additive_expr
      {$ast = new AstBinExpr(l($left, $right.ast), $op.op, $left, $right.ast);}
    | {$ast = $left;}
    ;

additive_expr
    returns [AstExpr ast]
    : left=multiplicative_expr additive_expr1[$left.ast]
      {$ast = $additive_expr1.ast;}
    ; 
additive_expr1
    [AstExpr left] returns [AstExpr ast]
    : op=additive_op right=multiplicative_expr
      {$left = new AstBinExpr(l($left, $right.ast), $op.op, $left, $right.ast);}
      additive_expr1[$left]
      {$ast = $additive_expr1.ast;}
    | {$ast = $left;}
    ;

multiplicative_expr
    returns [AstExpr ast]
    : left=prefix_expr multiplicative_expr1[$left.ast]
      {$ast = $multiplicative_expr1.ast;} 
    ;
multiplicative_expr1
    [AstExpr left] returns [AstExpr ast]
    : op=multiplicative_op right=prefix_expr
      {$left = new AstBinExpr(l($left, $right.ast), $op.op, $left, $right.ast);}
      multiplicative_expr1[$left]
      {$ast = $multiplicative_expr1.ast;} 
    | {$ast = $left;}
    ;

prefix_expr
    returns [AstExpr ast]
    : op=prefix_op e=prefix_expr {$ast = new AstPfxExpr(l($op.ast, $e.ast), $op.op, $e.ast);}
    | postfix_expr {$ast=$postfix_expr.ast;}
    ;

postfix_expr
    returns [AstExpr ast]
    : left=primary_expr postfix_expr1[$left.ast]
      {$ast = $postfix_expr1.ast; }
    ;
postfix_expr1
    [AstExpr left] returns [AstExpr ast]
    : lhs=postfix_op[$left] postfix_expr1[$lhs.ast]
      {$ast = $postfix_expr1.ast; }
    | {$ast = $left;}
    ;

primary_expr
    returns [AstExpr ast]
    : const_expr {$ast = $const_expr.ast;}
    | id_expr {$ast = $id_expr.ast;}
    | mem_expr {$ast = $mem_expr.ast;}
    | cast_expr {$ast = $cast_expr.ast;}
    ;
const_expr
    returns [AstExpr ast]
    : CONST_INT {$ast = new AstAtomExpr(
        l($CONST_INT), AstAtomExpr.Type.INT, $CONST_INT.text
        );}
    | CONST_CHAR {$ast = new AstAtomExpr(
        l($CONST_CHAR), AstAtomExpr.Type.CHAR, $CONST_CHAR.text
        );}
    | CONST_STR {$ast = new AstAtomExpr(
        l($CONST_STR), AstAtomExpr.Type.STR, $CONST_STR.text
        );}
    | NONE {$ast = new AstAtomExpr(
        l($NONE), AstAtomExpr.Type.VOID, $NONE.text
        );}
    | TRUE {$ast = new AstAtomExpr(
        l($TRUE), AstAtomExpr.Type.BOOL, $TRUE.text
        );}
    | FALSE {$ast = new AstAtomExpr(
        l($FALSE), AstAtomExpr.Type.BOOL, $FALSE.text
        );}
    | NIL {$ast = new AstAtomExpr(
        l($NIL), AstAtomExpr.Type.PTR, $NIL.text
        );}
    ;
id_expr
    returns [AstExpr ast]
    : ID id_expr1[$ID] {$ast = $id_expr1.ast;}
    ;
id_expr1
    [Token name] returns [AstExpr ast]
    : LPAR id_expr2 RPAR
      {$ast = new AstCallExpr(l($name, $RPAR), $name.getText(), $id_expr2.ast);}
    | {$ast = new AstNameExpr(l($name), $name.getText());}
    ;
id_expr2
    returns [AstTrees<AstExpr> ast]
    : expr id_expr3
      {$ast = new AstTrees<AstExpr>("arguments", cons($expr.ast, $id_expr3.ast));}
    | {$ast = new AstTrees<AstExpr>("arguments");}
    ;
id_expr3
    returns [LinkedList<AstExpr> ast]
    : COMMA expr id_expr3 {$ast = cons($expr.ast, $id_expr3.ast);}
    | {$ast = null;}
    ;
mem_expr
    returns [AstExpr ast]
    : NEW LPAR type RPAR {$ast = new AstNewExpr(l($NEW, $RPAR), $type.ast);}
    | DEL LPAR expr RPAR {$ast = new AstDelExpr(l($DEL, $RPAR), $expr.ast);}
    ; 
cast_expr
    returns [AstExpr ast]
    : LPAR expr t=cast_expr1 RPAR 
        {$ast = ($t.ast != null)
            ? new AstCastExpr(l($LPAR, $RPAR), $expr.ast, $t.ast)
            : $expr.ast; }
    ;
cast_expr1
    returns [AstType ast]
    : COLON type {$ast = $type.ast;}
    | {$ast = null;}
    ;

// Statement
statement
    returns [AstStmt ast]
    : assign_statement {$ast = $assign_statement.ast;}
    | if_statement {$ast = $if_statement.ast;}
    | while_statement {$ast = $while_statement.ast;}
    | let_statement {$ast = $let_statement.ast;}
    | block_statement {$ast = $block_statement.ast;}
    ;

assign_statement
    returns [AstStmt ast]
    : expr src=assign_statement1 
        { $ast = ($src.ast != null)
            ? new AstAssignStmt(l($expr.ast, $src.ast), $expr.ast, $src.ast)
            : new AstExprStmt(l($expr.ast), $expr.ast);
        }
    ;
assign_statement1
    returns [AstExpr ast]
    : ASS expr {$ast = $expr.ast;}
    | {$ast = null;}
    ;

if_statement
    returns [AstIfStmt ast]
    : IF expr THEN a=if_statement1 
        {$ast= new AstIfStmt(l($IF, $a.ifthen, $a.ifelse), $expr.ast, $a.ifthen, $a.ifelse);}
    ;
if_statement1
    returns [AstStmt ifthen, AstStmt ifelse]
    : statement {$ifthen=$statement.ast;$ifelse=null;}
    | a=statement ELSE b=statement {$ifthen=$a.ast;$ifelse=$b.ast;}
    ;

while_statement
    returns [AstWhileStmt ast]
    : WHILE expr DO statement
        {$ast = new AstWhileStmt(l($WHILE, $statement.ast), $expr.ast, $statement.ast);}
    ;

let_statement
    returns [AstDeclStmt ast]
    : LET decl IN statement 
        {$ast = new AstDeclStmt(l($LET, $statement.ast), $decl.ast, $statement.ast);}

    ;
block_statement
    returns [AstStmts ast]
    : LCURLY statement block_statement1 RCURLY 
      {$ast = new AstStmts(
            l($LCURLY, $RCURLY),
            new AstTrees<AstStmt>("block statements", cons($statement.ast, $block_statement1.ast))
            );
        }
    ;
block_statement1
    returns [LinkedList<AstStmt> ast]
    : SEMI statement block_statement1 {$ast = cons($statement.ast, $block_statement1.ast);}
    | {$ast = null;}
    ;

// Operators

disjunctive_op
    returns [AstBinExpr.Oper op]
    : OR {$op = AstBinExpr.Oper.OR;}
    ;
conjunctive_op
    returns [AstBinExpr.Oper op]
    : AND {$op = AstBinExpr.Oper.AND;}
    ;
relational_op
    returns [AstBinExpr.Oper op]
    : EQ {$op = AstBinExpr.Oper.EQU;}
    | NEQ {$op = AstBinExpr.Oper.NEQ;}
    | LT {$op = AstBinExpr.Oper.LTH;}
    | GT {$op = AstBinExpr.Oper.GTH;}
    | LEQ {$op = AstBinExpr.Oper.LEQ;}
    | GEQ {$op = AstBinExpr.Oper.GEQ;}
    ;
additive_op
    returns [AstBinExpr.Oper op]
    : PLUS {$op = AstBinExpr.Oper.ADD;}
    | MINUS {$op = AstBinExpr.Oper.SUB;}
    ;
multiplicative_op
    returns [AstBinExpr.Oper op]
    : MUL {$op = AstBinExpr.Oper.MUL;}
    | DIV {$op = AstBinExpr.Oper.DIV;}
    | MOD {$op = AstBinExpr.Oper.MOD;}
    ;
prefix_op
    returns [Token ast, AstPfxExpr.Oper op]
    : NOT {$ast = $NOT; $op = AstPfxExpr.Oper.NOT;}
    | PLUS {$ast = $PLUS; $op = AstPfxExpr.Oper.ADD;}
    | MINUS {$ast = $MINUS; $op = AstPfxExpr.Oper.SUB;}
    | HAT {$ast = $HAT; $op = AstPfxExpr.Oper.PTR;}
    ;
postfix_op
    [AstExpr left] returns [AstExpr ast]
    : LBRACK expr RBRACK
      {$ast = new AstArrExpr(l(left, $RBRACK), $left, $expr.ast);}
    | HAT
      {$ast = new AstSfxExpr(l(left, $HAT), AstSfxExpr.Oper.PTR, $left);}
    | DOT ID
      {$ast = new AstRecExpr(
        l(left, $ID), $left,
        new AstNameExpr(l($ID), $ID.text)
       );}
    ;
