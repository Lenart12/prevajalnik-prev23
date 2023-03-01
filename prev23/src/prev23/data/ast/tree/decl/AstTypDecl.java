package prev23.data.ast.tree.decl;

import prev23.common.report.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.*;

/**
 * Type declaration.
 */
public class AstTypDecl extends AstNameDecl implements AstDecl {

	/** The representation of this type. */
	public final AstType type;

	/**
	 * Constructs a type declaration.
	 * 
	 * @param location The location.
	 * @param name     The name of this type.
	 * @param type     The representation of this type.
	 */
	public AstTypDecl(Location location, String name, AstType type) {
		super(location, name);
		this.type = type;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
