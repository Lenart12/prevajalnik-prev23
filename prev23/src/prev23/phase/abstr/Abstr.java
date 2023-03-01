package prev23.phase.abstr;

import prev23.phase.*;
import prev23.data.ast.tree.*;

/**
 * Abstract syntax tree construction.
 */
public class Abstr extends Phase {

	/** The abstract syntax tree. */
	public static AstTree tree;
		
	/**
	 * Phase construction.
	 */
	public Abstr() {
		super("abstr");
	}

}
