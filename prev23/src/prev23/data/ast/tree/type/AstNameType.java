package prev23.data.ast.tree.type;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * Type name.
 */
public class AstNameType extends AstNode implements AstType, AstName {

	/** The name. */
	public final String name;

	/**
	 * Constructs a type name.
	 * 
	 * @param location the Location.
	 * @param name     The name.
	 */
	public AstNameType(Location location, String name) {
		super(location);
		this.name = name;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
