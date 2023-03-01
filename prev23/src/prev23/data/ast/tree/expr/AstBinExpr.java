package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * Binary expression.
 */
public class AstBinExpr extends AstNode implements AstExpr {

	/** Operators. */
	public enum Oper {
		/** Logial or. */
		OR,
		/** Logical and. */
		AND,
		/** Equal. */
		EQU,
		/** Not equal. */
		NEQ,
		/** Less than. */
		LTH,
		/** Greater than. */
		GTH,
		/** Less of equal. */
		LEQ,
		/** Greater or equal. */
		GEQ,
		/** Add. */
		ADD,
		/** Substract. */
		SUB,
		/** Multiply. */
		MUL,
		/** Divide. */
		DIV,
		/** Modulo. */
		MOD
	};

	/** The operator. */
	public final Oper oper;

	/** The first subexpression. */
	public final AstExpr fstExpr;

	/** The second subexpression. */
	public final AstExpr sndExpr;

	/**
	 * Constructs a binary expression.
	 * 
	 * @param location The location.
	 * @param oper     The operator.
	 * @param fstExpr  The first subexpression.
	 * @param sndExpr  The second subexpression.
	 */
	public AstBinExpr(Location location, Oper oper, AstExpr fstExpr, AstExpr sndExpr) {
		super(location);
		this.oper = oper;
		this.fstExpr = fstExpr;
		this.sndExpr = sndExpr;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
