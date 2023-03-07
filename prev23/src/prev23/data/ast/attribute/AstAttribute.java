package prev23.data.ast.attribute;

import java.util.*;

import prev23.data.ast.tree.*;

/**
 * An attribute of the abstract syntax tree node.
 *
 * @param <Node>  Nodes that values are associated with.
 * @param <Value> Values associated with nodes.
 */
public class AstAttribute<Node extends AstTree, Value> {

	/** Mapping of nodes to values. */
	private Vector<Value> mapping;

	/**
	 * Constructs a new attribute.
	 */
	public AstAttribute() {
		mapping = new Vector<Value>();
	}

	/**
	 * Associates a value with the specified abstract syntax tree node.
	 * 
	 * @param node  The specified abstract syntax tree node.
	 * @param value The value.
	 * @return The value.
	 */
	public Value put(Node node, Value value) {
		int id = node.id();
		while (id >= mapping.size())
			mapping.setSize(id + 1000);
		mapping.set(id, value);
		return value;
	}

	/**
	 * Returns a value associated with the specified abstract syntax tree node.
	 * 
	 * @param node The specified abstract syntax tree node.
	 * @return The value (or {@code null} if the value is not found).
	 */
	public Value get(Node node) {
		int id = node.id();
		while (id >= mapping.size())
			return null;
		return mapping.get(id);
	}

}
