package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * Suffix expression.
 */
public class AstSfxExpr extends AstNode implements AstExpr {

	/** Operators. */
	public enum Oper {
		/** Dereference. */
		PTR
	};

	/** The operator. */
	public final Oper oper;

	/** The subexpression. */
	public final AstExpr expr;

	/**
	 * Constructs a suffix expression.
	 * 
	 * @param location The location.
	 * @param oper     The operator.
	 * @param expr     The subexpression.
	 */
	public AstSfxExpr(Location location, Oper oper, AstExpr expr) {
		super(location);
		this.oper = oper;
		this.expr = expr;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
