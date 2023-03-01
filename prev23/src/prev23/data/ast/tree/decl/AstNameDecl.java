package prev23.data.ast.tree.decl;

import prev23.common.report.*;
import prev23.data.ast.tree.*;

/**
 * Abstract name declaration.
 */
public abstract class AstNameDecl extends AstNode {

	/** The declared name. */
	public final String name;

	/**
	 * Constructs an abstract declaration of a name.
	 * 
	 * @param location The location.
	 * @param name     The declared name.
	 */
	public AstNameDecl(Location location, String name) {
		super(location);
		this.name = name;
	}

}
