package prev23.phase.memory;

import prev23.common.logger.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.AstAtomExpr;
import prev23.data.ast.visitor.*;
import prev23.data.mem.*;

/**
 * Logs memory attributes of an abstract syntax tree.
 * 
 * (Must be used as a subvisitor of {@link prev.phase.abstr.AbsLogger}.)
 */
public class MemLogger extends AstNullVisitor<Object, Object> {

	/** The logger the log should be written to. */
	private final Logger logger;

	/**
	 * Construct a new visitor with a logger the log should be written to.
	 * 
	 * @param logger The logger the log should be written to.
	 */
	public MemLogger(Logger logger) {
		this.logger = logger;
	}

	// DECLARATIONS

	@Override
	public Object visit(AstCmpDecl cmpDecl, Object arg) {
		MemAccess access = Memory.accesses.get(cmpDecl);
		if (access != null)
			access.log(logger);
		return null;
	}

	@Override
	public Object visit(AstFunDecl funDecl, Object arg) {
		MemFrame frame = Memory.frames.get(funDecl);
		if (frame != null)
			frame.log(logger);
		return null;
	}

	@Override
	public Object visit(AstParDecl parDecl, Object arg) {
		MemAccess access = Memory.accesses.get(parDecl);
		if (access != null)
			access.log(logger);
		return null;
	}

	@Override
	public Object visit(AstVarDecl varDecl, Object arg) {
		MemAccess access = Memory.accesses.get(varDecl);
		if (access != null)
			access.log(logger);
		return null;
	}

	// EXPRESSIONS

	@Override
	public Object visit(AstAtomExpr atomExpr, Object arg) {
		switch (atomExpr.type) {
		case STR:
			MemAbsAccess access = Memory.strings.get(atomExpr);
			if (access != null)
				access.log(logger);
			break;
		default:
			break;
		}
		return null;
	}

}
