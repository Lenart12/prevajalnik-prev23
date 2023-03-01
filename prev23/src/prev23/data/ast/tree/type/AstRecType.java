package prev23.data.ast.tree.type;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.visitor.*;

/**
 * Record type.
 */
public class AstRecType extends AstNode implements AstType {

	/** Components. */
	public final AstTrees<AstCmpDecl> comps;

	/**
	 * Constructs a record type.
	 * 
	 * @param location The location.
	 * @param comps    The components.
	 */
	public AstRecType(Location location, AstTrees<AstCmpDecl> comps) {
		super(location);
		this.comps = comps;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
