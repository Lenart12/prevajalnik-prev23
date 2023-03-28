package prev23.phase.imcgen;

import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.attribute.*;
import prev23.data.ast.visitor.*;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.code.stmt.*;
import prev23.phase.*;

/**
 * Intermediate code generation.
 */
public class ImcGen extends Phase implements AstVisitor<Object, Object> {

	/** Maps statements to intermediate code. */
	public static final AstAttribute<AstStmt, ImcStmt> stmtImc = new AstAttribute<AstStmt, ImcStmt>();

	/** Maps expressions to intermediate code. */
	public static final AstAttribute<AstExpr, ImcExpr> exprImc = new AstAttribute<AstExpr, ImcExpr>();

	/**
	 * Constructs a new phase for intermediate code generation.
	 */
	public ImcGen() {
		super("imcgen");
	}

}
