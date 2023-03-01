package prev23.data.ast.tree.decl;

import prev23.common.report.*;
import prev23.data.ast.tree.type.*;

/**
 * Any variable declaration.
 */
public abstract class AstMemDecl extends AstNameDecl {

	/** The type of this variable. */
	public final AstType type;

	/**
	 * Constructs a variable declaration.
	 * 
	 * @param location The location.
	 * @param name     The name of this variable.
	 * @param type     The type of this variable.
	 */
	protected AstMemDecl(Location location, String name, AstType type) {
		super(location, name);
		this.type = type;
	}

}
