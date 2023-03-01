package prev23.data.ast.tree.decl;

import prev23.common.report.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.*;

/**
 * Component declaration.
 */
public class AstCmpDecl extends AstMemDecl {

	/**
	 * Constructs a component declaration.
	 * 
	 * @param location The location.
	 * @param name     The name of this component.
	 * @param type     The type of this component.
	 */
	public AstCmpDecl(Location location, String name, AstType type) {
		super(location, name, type);
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
