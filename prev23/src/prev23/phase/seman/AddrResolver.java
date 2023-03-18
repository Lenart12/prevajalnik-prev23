package prev23.phase.seman;

import prev23.common.report.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.visitor.*;


public class AddrResolver extends AstFullVisitor<Boolean, Object> {
	private void UnexpectedNull() {
		throw new Report.InternalError();
	}

	private boolean declare_address(AstExpr ast, boolean is_address) {
		SemAn.isAddr.put(ast, is_address);
		return is_address;
	}

	// EXPRESSIONS

	@Override
	public Boolean visit(AstArrExpr arrExpr, Object arg) {
		if (arrExpr.arr == null) UnexpectedNull();
		if (arrExpr.idx == null) UnexpectedNull();
		arrExpr.idx.accept(this, null);
		return declare_address(arrExpr, arrExpr.arr.accept(this, arg));
	}

	@Override
	public Boolean visit(AstAtomExpr atomExpr, Object arg) {
		return declare_address(atomExpr, false);
	}

	@Override
	public Boolean visit(AstBinExpr binExpr, Object arg) {
		super.visit(binExpr, arg);
		return declare_address(binExpr, false);
	}

	@Override
	public Boolean visit(AstCallExpr callExpr, Object arg) {
		super.visit(callExpr, arg);
		return declare_address(callExpr, false);
	}

	@Override
	public Boolean visit(AstCastExpr castExpr, Object arg) {
		super.visit(castExpr, arg);
		return declare_address(castExpr, false);
	}

	@Override
	public Boolean visit(AstDelExpr delExpr, Object arg) {
		super.visit(delExpr, arg);
		return declare_address(delExpr, false);
	}

	@Override
	public Boolean visit(AstNameExpr nameExpr, Object arg) {
		var declared_at = SemAn.declaredAt.get(nameExpr);
		if (declared_at == null) UnexpectedNull();

		if (declared_at instanceof AstVarDecl || declared_at instanceof AstParDecl) {
			return declare_address(nameExpr, true);
		}

		return declare_address(nameExpr, false);
	}

	@Override
	public Boolean visit(AstNewExpr newExpr, Object arg) {
		super.visit(newExpr, arg);
		return declare_address(newExpr, false);
	}

	@Override
	public Boolean visit(AstPfxExpr pfxExpr, Object arg) {
		super.visit(pfxExpr, arg);
		return declare_address(pfxExpr, false);
	}

	@Override
	public Boolean visit(AstRecExpr recExpr, Object arg) {
		if (recExpr.rec == null) UnexpectedNull();
		if (recExpr.comp == null) UnexpectedNull();

		return declare_address(recExpr, recExpr.rec.accept(this, arg));
	}

	@Override
	public Boolean visit(AstSfxExpr sfxExpr, Object arg) {
		super.visit(sfxExpr, arg);
		return declare_address(sfxExpr, sfxExpr.oper == AstSfxExpr.Oper.PTR);
	}
}