package prev23.data.ast.tree.decl;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.*;

/**
 * Any function declaration.
 */
public class AstFunDecl extends AstNameDecl implements AstDecl {

	/** The parameters of this function. */
	public final AstTrees<AstParDecl> pars;

	/** The type of this function. */
	public final AstType type;

	/** The statement of this function. */
	public final AstStmt stmt;

	/**
	 * Constructs a function declaration.
	 * 
	 * @param location The location.
	 * @param name     The name of this function.
	 * @param pars     The parameters of this function.
	 * @param type     The type of this function.
	 * @param stmt     The statement of this function.
	 */
	public AstFunDecl(Location location, String name, AstTrees<AstParDecl> pars, AstType type, AstStmt stmt) {
		super(location, name);
		this.pars = pars;
		this.type = type;
		this.stmt = stmt;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
