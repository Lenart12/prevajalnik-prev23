package prev23.data.ast.visitor;

import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.tree.type.*;

/**
 * Abstract syntax tree visitor that does nothing.
 * 
 * @param <Result> The result type.
 * @param <Arg>    The argument type.
 */
public class AstNullVisitor<Result, Arg> implements AstVisitor<Result, Arg> {

	// GENERAL PURPOSE

	@Override
	public Result visit(AstTrees<? extends AstTree> trees, Arg arg) {
		return null;
	}

	// DECLARATIONS

	@Override
	public Result visit(AstCmpDecl compDecl, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstFunDecl funDecl, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstParDecl parDecl, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstTypDecl typeDecl, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstVarDecl varDecl, Arg arg) {
		return null;
	}

	// EXPRESSIONS

	@Override
	public Result visit(AstArrExpr arrExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstAtomExpr atomExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstBinExpr binExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstCallExpr callExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstCastExpr castExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstDelExpr delExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstNameExpr nameExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstNewExpr newExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstPfxExpr pfxExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstRecExpr recExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstSfxExpr sfxExpr, Arg arg) {
		return null;
	}

	// STATEMENTS

	@Override
	public Result visit(AstAssignStmt assignStmt, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstDeclStmt declStmt, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstExprStmt exprStmt, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstIfStmt ifStmt, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstStmts stmts, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstWhileStmt whileStmt, Arg arg) {
		return null;
	}

	// TYPES

	@Override
	public Result visit(AstArrType arrType, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstAtomType atomType, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstNameType nameType, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstPtrType ptrType, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstRecType recType, Arg arg) {
		return null;
	}

}
