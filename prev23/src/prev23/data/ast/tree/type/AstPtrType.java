package prev23.data.ast.tree.type;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * Pointer type.
 */
public class AstPtrType extends AstNode implements AstType {

	/** Base type. */
	public final AstType baseType;

	/**
	 * Constructs a pointer type.
	 * 
	 * @param location The location.
	 * @param baseType The base type.
	 */
	public AstPtrType(Location location, AstType baseType) {
		super(location);
		this.baseType = baseType;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
