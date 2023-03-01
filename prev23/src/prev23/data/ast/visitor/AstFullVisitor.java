package prev23.data.ast.visitor;

import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.tree.type.*;

/**
 * Abstract syntax tree visitor that traverses the abstract syntax tree.
 * 
 * @param <Result> The result type.
 * @param <Arg>    The argument type.
 */
public class AstFullVisitor<Result, Arg> implements AstVisitor<Result, Arg> {

	// GENERAL PURPOSE

	@Override
	public Result visit(AstTrees<? extends AstTree> trees, Arg arg) {
		for (AstTree t : trees)
			if (t != null)
				t.accept(this, arg);
		return null;
	}

	// DECLARATIONS

	@Override
	public Result visit(AstCmpDecl compDecl, Arg arg) {
		if (compDecl.type != null)
			compDecl.type.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstFunDecl funDecl, Arg arg) {
		if (funDecl.pars != null)
			funDecl.pars.accept(this, arg);
		if (funDecl.type != null)
			funDecl.type.accept(this, arg);
		if (funDecl.stmt != null)
			funDecl.stmt.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstParDecl parDecl, Arg arg) {
		if (parDecl.type != null)
			parDecl.type.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstTypDecl typeDecl, Arg arg) {
		if (typeDecl.type != null)
			typeDecl.type.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstVarDecl varDecl, Arg arg) {
		if (varDecl.type != null)
			varDecl.type.accept(this, arg);
		return null;
	}

	// EXPRESSIONS

	@Override
	public Result visit(AstArrExpr arrExpr, Arg arg) {
		if (arrExpr.arr != null)
			arrExpr.arr.accept(this, arg);
		if (arrExpr.idx != null)
			arrExpr.idx.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstAtomExpr atomExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstBinExpr binExpr, Arg arg) {
		if (binExpr.fstExpr != null)
			binExpr.fstExpr.accept(this, arg);
		if (binExpr.sndExpr != null)
			binExpr.sndExpr.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstCallExpr callExpr, Arg arg) {
		if (callExpr.args != null)
			callExpr.args.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstCastExpr castExpr, Arg arg) {
		if (castExpr.expr != null)
			castExpr.expr.accept(this, arg);
		if (castExpr.type != null)
			castExpr.type.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstDelExpr delExpr, Arg arg) {
		if (delExpr.expr != null)
			delExpr.expr.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstNameExpr nameExpr, Arg arg) {
		return null;
	}

	@Override
	public Result visit(AstNewExpr newExpr, Arg arg) {
		if (newExpr.type != null)
			newExpr.type.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstPfxExpr pfxExpr, Arg arg) {
		if (pfxExpr.expr != null)
			pfxExpr.expr.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstRecExpr recExpr, Arg arg) {
		if (recExpr.rec != null)
			recExpr.rec.accept(this, arg);
		if (recExpr.comp != null)
			recExpr.comp.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstSfxExpr sfxExpr, Arg arg) {
		if (sfxExpr.expr != null)
			sfxExpr.expr.accept(this, arg);
		return null;
	}

	// STATEMENTS

	@Override
	public Result visit(AstAssignStmt assignStmt, Arg arg) {
		if (assignStmt.dst != null)
			assignStmt.dst.accept(this, arg);
		if (assignStmt.src != null)
			assignStmt.src.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstDeclStmt declStmt, Arg arg) {
		if (declStmt.decls != null)
			declStmt.decls.accept(this, arg);
		if (declStmt.stmt != null)
			declStmt.stmt.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstExprStmt exprStmt, Arg arg) {
		if (exprStmt.expr != null)
			exprStmt.expr.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstIfStmt ifStmt, Arg arg) {
		if (ifStmt.cond != null)
			ifStmt.cond.accept(this, arg);
		if (ifStmt.thenStmt != null)
			ifStmt.thenStmt.accept(this, arg);
		if (ifStmt.elseStmt != null)
			ifStmt.elseStmt.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstStmts stmts, Arg arg) {
		if (stmts.stmts != null)
			stmts.stmts.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstWhileStmt whileStmt, Arg arg) {
		if (whileStmt.cond != null)
			whileStmt.cond.accept(this, arg);
		if (whileStmt.bodyStmt != null)
			whileStmt.bodyStmt.accept(this, arg);
		return null;
	}

	// TYPES

	@Override
	public Result visit(AstArrType arrType, Arg arg) {
		if (arrType.elemType != null)
			arrType.elemType.accept(this, arg);
		if (arrType.numElems != null)
			arrType.numElems.accept(this, arg);
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
		if (ptrType.baseType != null)
			ptrType.baseType.accept(this, arg);
		return null;
	}

	@Override
	public Result visit(AstRecType recType, Arg arg) {
		if (recType.comps != null)
			recType.comps.accept(this, arg);
		return null;
	}

}
