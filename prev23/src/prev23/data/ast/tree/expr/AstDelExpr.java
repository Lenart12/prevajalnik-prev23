package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * New expression.
 */
public class AstDelExpr extends AstNode implements AstExpr {

	/** The pointer expression. */
	public final AstExpr expr;

	/**
	 * Constructs a new expression.
	 * 
	 * @param location The location.
	 * @param expr     The pointer expression.
	 */
	public AstDelExpr(Location location, AstExpr expr) {
		super(location);
		this.expr = expr;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
