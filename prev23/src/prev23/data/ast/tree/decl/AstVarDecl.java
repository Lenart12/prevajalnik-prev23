package prev23.data.ast.tree.decl;

import prev23.common.report.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.*;

/**
 * Variable declaration.
 */
public class AstVarDecl extends AstMemDecl implements AstDecl {

	/**
	 * Constructs a variable declaration.
	 * 
	 * @param location The location.
	 * @param name     The name of this variable.
	 * @param type     The type of this variable.
	 */
	public AstVarDecl(Location location, String name, AstType type) {
		super(location, name, type);
	}
	
	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
