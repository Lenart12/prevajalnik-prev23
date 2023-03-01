package prev23.data.ast.tree;

import prev23.common.report.*;
import prev23.data.ast.visitor.*;

/**
 * Abstract syntax tree.
 */
public interface AstTree extends Cloneable, Locatable {

	/**
	 * Returns the unique id of this node.
	 * 
	 * @return The unique id of this node.
	 */
	public abstract int id();

	/** The acceptor method. */

	/**
	 * The acceptor method.
	 * 
	 * @param <Result> The result type.
	 * @param <Arg>    The argument type.
	 * @param visitor  The visitor accepted by this acceptor.
	 * @param arg      The argument.
	 * @return The result.
	 */
	public abstract <Result, Arg> Result accept(AstVisitor<Result, Arg> visitor, Arg arg);

}
