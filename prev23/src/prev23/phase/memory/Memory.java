package prev23.phase.memory;

import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.attribute.*;
import prev23.data.mem.*;
import prev23.phase.*;

/**
 * Computing memory layout: frames and accesses.
 */
public class Memory extends Phase {

	/** Maps function declarations to frames. */
	public static final AstAttribute<AstFunDecl, MemFrame> frames = new AstAttribute<AstFunDecl, MemFrame>();

	/** Maps variable declarations to accesses. */
	public static final AstAttribute<AstMemDecl, MemAccess> accesses = new AstAttribute<AstMemDecl, MemAccess>();

	/** Maps string constants to accesses. */
	public static final AstAttribute<AstAtomExpr, MemAbsAccess> strings = new AstAttribute<AstAtomExpr, MemAbsAccess>();

	/**
	 * Constructs a new phase for computing layout.
	 */
	public Memory() {
		super("memory");
	}

}
