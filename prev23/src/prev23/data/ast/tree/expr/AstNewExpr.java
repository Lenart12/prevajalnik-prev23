package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.*;

/**
 * New expression.
 */
public class AstNewExpr extends AstNode implements AstExpr {

	/** The type. */
	public final AstType type;

	/**
	 * Constructs a new expression.
	 * 
	 * @param location The location.
	 * @param type     The type.
	 */
	public AstNewExpr(Location location, AstType type) {
		super(location);
		this.type = type;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
