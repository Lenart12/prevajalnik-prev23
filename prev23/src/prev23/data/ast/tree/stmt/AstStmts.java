package prev23.data.ast.tree.stmt;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * A block of statements.
 */
public class AstStmts extends AstNode implements AstStmt {

	/** The statements. */
	public final AstTrees<AstStmt> stmts;

	/**
	 * Construct a block of statements.
	 * 
	 * @param location The location.
	 * @param stmts    The statements.
	 */
	public AstStmts(Location location, AstTrees<AstStmt> stmts) {
		super(location);
		this.stmts = stmts;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
