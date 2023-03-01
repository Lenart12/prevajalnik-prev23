package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * Prefix expression.
 */
public class AstPfxExpr extends AstNode implements AstExpr {

	/** Operators. */
	public enum Oper {
		/** Sign. */
		ADD,
		/** Sign. */
		SUB,
		/** Negation. */
		NOT,
		/** Reference. */
		PTR
	};

	/** The operator. */
	public final Oper oper;

	/** The subexpression. */
	public final AstExpr expr;

	/**
	 * Constructs a prefix expression.
	 * 
	 * @param location The location.
	 * @param oper     The operator.
	 * @param expr     The subexpression.
	 */
	public AstPfxExpr(Location location, Oper oper, AstExpr expr) {
		super(location);
		this.oper = oper;
		this.expr = expr;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
