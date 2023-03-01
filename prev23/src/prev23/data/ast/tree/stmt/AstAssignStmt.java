package prev23.data.ast.tree.stmt;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.visitor.*;

/**
 * Assignment statement.
 */
public class AstAssignStmt extends AstNode implements AstStmt {

	/** The destination. */
	public final AstExpr dst;

	/** The source. */
	public final AstExpr src;

	/**
	 * Construct an assignment statement.
	 * 
	 * @param location The location.
	 * @param dst      The destination.
	 * @param src      The source.
	 */
	public AstAssignStmt(Location location, AstExpr dst, AstExpr src) {
		super(location);
		this.dst = dst;
		this.src = src;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
