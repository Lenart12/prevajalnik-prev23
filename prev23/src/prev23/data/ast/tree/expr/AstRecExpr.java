package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * Record component access expression.
 */
public class AstRecExpr extends AstNode implements AstExpr {

	/** The record. */
	public final AstExpr rec;

	/** The component. */
	public final AstNameExpr comp;

	/**
	 * Constructs a record component access expression.
	 * 
	 * @param location The location.
	 * @param rec      The record.
	 * @param comp     The component.
	 */
	public AstRecExpr(Location location, AstExpr rec, AstNameExpr comp) {
		super(location);
		this.rec = rec;
		this.comp = comp;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
