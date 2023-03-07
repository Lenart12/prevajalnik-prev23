package prev23.phase.seman;

import prev23.phase.*;
import prev23.data.ast.attribute.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.type.*;
import prev23.data.typ.*;

/**
 * Semantic analysis.
 */
public class SemAn extends Phase {

	/** Maps names to declarations. */
	public static final AstAttribute<AstName, AstNameDecl> declaredAt = new AstAttribute<AstName, AstNameDecl>();

	/** Maps type declarations to semantic representations of types. */
	public static final AstAttribute<AstTypDecl, SemName> declaresType = new AstAttribute<AstTypDecl, SemName>();

	/** Maps syntax types to semantic representations of types. */
	public static final AstAttribute<AstType, SemType> isType = new AstAttribute<AstType, SemType>();

	/** Maps syntax expressions to semantic representations of types. */
	public static final AstAttribute<AstExec, SemType> ofType = new AstAttribute<AstExec, SemType>();

	/** Indicates which syntax expressions denote lvalues. */
	public static final AstAttribute<AstExpr, Boolean> isAddr = new AstAttribute<AstExpr, Boolean>();

	/**
	 * Phase construction.
	 */
	public SemAn() {
		super("seman");
	}

}
