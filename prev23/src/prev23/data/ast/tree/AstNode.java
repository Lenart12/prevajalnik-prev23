package prev23.data.ast.tree;

import prev23.common.report.*;

/**
 * An abstract node of an abstract syntax tree.
 */
public abstract class AstNode implements AstTree {

	/** The number of nodes constructed so far. */
	private static int numNodes = 0;

	/** The unique id of this node. */
	public final int id;

	/** The location of this node. */
	private Location location;

	/**
	 * Constructs an abstract node of an abstract syntax tree.
	 * 
	 * @param location The location.
	 */
	public AstNode(Location location) {
		id = numNodes++;
		this.location = location;
	}

	@Override
	public final int id() {
		return id;
	}

	@Override
	public final void relocate(Location location) {
		this.location = location;
	}

	@Override
	public final Location location() {
		return location;
	}

}
