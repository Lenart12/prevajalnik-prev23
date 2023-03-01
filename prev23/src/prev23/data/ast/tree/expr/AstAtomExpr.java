package prev23.data.ast.tree.expr;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.visitor.*;

/**
 * Atom expression, i.e., a constant.
 */
public class AstAtomExpr extends AstNode implements AstExpr {

	/** Types. */
	public enum Type {
		/** Constant of type void. */
		VOID,
		/** Constant of type char. */
		CHAR,
		/** Constant of type int. */
		INT,
		/** Constant of type bool. */
		BOOL,
		/** Constant of a pointer type. */
		PTR,
		/** Constant of type string. */
		STR
	};

	/** The type of a constant. */
	public final Type type;

	/** The value of a constant. */
	public final String value;

	/**
	 * Constructs an atom expression, i.e., a constant.
	 * 
	 * @param location The location.
	 * @param type     The type of a constant.
	 * @param value    The value of a constant.
	 */
	public AstAtomExpr(Location location, Type type, String value) {
		super(location);
		this.type = type;
		this.value = value;
	}

	@Override
	public <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

}
