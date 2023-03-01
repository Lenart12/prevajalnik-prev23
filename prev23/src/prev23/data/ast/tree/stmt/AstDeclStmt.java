package prev23.data.ast.tree.stmt;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.visitor.*;

/**
 * Statement with declarations.
 */
public class AstDeclStmt extends AstNode implements AstStmt {

	/** The declarations. */
	public final AstTrees<AstTrees<AstDecl>> decls;

	/** The statement. */
	public final AstStmt stmt;

	/**
	 * Construct a statement with declarations.
	 * 
	 * @param location The location.
	 * @param decls    The declarations.
	 * @param stmt     The source.
	 */
	public AstDeclStmt(Location location, AstTrees<AstTrees<AstDecl>> decls, AstStmt stmt) {
		super(location);
		this.decls = decls;
		this.stmt = stmt;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
