package prev23.data.ast.tree.stmt;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.visitor.*;

/**
 * While statement.
 */
public class AstWhileStmt extends AstNode implements AstStmt {

	/** The condition. */
	public final AstExpr cond;

	/** The body statement. */
	public final AstStmt bodyStmt;

	/**
	 * Constructs a while statement.
	 * 
	 * @param location The location.
	 * @param cond     The condition.
	 * @param bodyStmt The body statement.
	 */
	public AstWhileStmt(Location location, AstExpr cond, AstStmt bodyStmt) {
		super(location);
		this.cond = cond;
		this.bodyStmt = bodyStmt;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
