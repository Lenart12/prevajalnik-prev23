package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.*;

/**
 * Cast expression.
 */
public class AstCastExpr extends AstNode implements AstExpr {

	/** The expression. */
	public final AstExpr expr;

	/** The type. */
	public final AstType type;

	/**
	 * Constructs a cast expression.
	 * 
	 * @param location The location.
	 * @param expr     The expression.
	 * @param type     The type.
	 */
	public AstCastExpr(Location location, AstExpr expr, AstType type) {
		super(location);
		this.expr = expr;
		this.type = type;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
