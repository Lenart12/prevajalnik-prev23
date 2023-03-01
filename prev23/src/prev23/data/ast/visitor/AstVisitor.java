package prev23.data.ast.visitor;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.tree.type.*;

/**
 * Abstract syntax tree visitor.
 * 
 * @param <Result> The result type.
 * @param <Arg>    The argument type.
 */
public interface AstVisitor<Result, Arg> {

	// GENERAL PURPOSE

	public default Result visit(AstTrees<? extends AstTree> trees, Arg arg) {
		throw new Report.InternalError();
	}

	// DECLARATIONS

	public default Result visit(AstCmpDecl compDecl, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstFunDecl funDecl, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstParDecl parDecl, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstTypDecl typeDecl, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstVarDecl varDecl, Arg arg) {
		throw new Report.InternalError();
	}

	// EXPRESSIONS

	public default Result visit(AstArrExpr arrExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstAtomExpr atomExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstBinExpr binExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstCallExpr callExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstCastExpr castExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstDelExpr delExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstNameExpr nameExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstNewExpr newExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstPfxExpr pfxExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstRecExpr recExpr, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstSfxExpr sfxExpr, Arg arg) {
		throw new Report.InternalError();
	}

	// STATEMENTS

	public default Result visit(AstAssignStmt assignStmt, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstDeclStmt declStmt, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstExprStmt exprStmt, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstIfStmt ifStmt, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstStmts stmts, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstWhileStmt whileStmt, Arg arg) {
		throw new Report.InternalError();
	}

	// TYPES

	public default Result visit(AstArrType arrType, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstAtomType atomType, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstNameType nameType, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstPtrType ptrType, Arg arg) {
		throw new Report.InternalError();
	}

	public default Result visit(AstRecType recType, Arg arg) {
		throw new Report.InternalError();
	}

}
