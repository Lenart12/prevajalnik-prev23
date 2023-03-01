package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * Variable access or a parameterless subprogram call.
 */
public class AstNameExpr extends AstNode implements AstExpr, AstName {

	/** The name. */
	public final String name;

	/**
	 * Constructs a variable access or a parameterless subprogram call.
	 * 
	 * @param location The location.
	 * @param name     The name.
	 */
	public AstNameExpr(Location location, String name) {
		super(location);
		this.name = name;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
