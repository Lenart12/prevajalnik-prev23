package prev23.phase.seman;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.visitor.*;
import prev23.phase.seman.*;

/**
 * Name resolver.
 * 
 * Name resolver connects each node of a abstract syntax tree where a name is
 * used with the node where it is declared. The only exceptions are a record
 * field names which are connected with its declarations by type resolver. The
 * Objects of the name resolver are stored in
 * {@link prev23.phase.seman.SemAn#declaredAt}.
 */
public class NameResolver extends AstFullVisitor<Object, Object> {
	SymbTable symbols;
	SemAn seman;

	public NameResolver(SemAn seman) {
		symbols = new SymbTable();
		seman = seman;
	}

	private void ins(String name, AstNameDecl decl) {
		try {
			symbols.ins(decl.name, decl);
			System.out.printf("Name %s declared at %s%n", name, decl.location());
		} catch (SymbTable.CannotInsNameException e) {
			throw new Report.Error(decl.location(), String.format("Duplicate symbol %s", name));
		}
	}

	private AstNameDecl fnd(AstName ast_name, String name) {
		try {
			var decl = symbols.fnd(name);
			System.out.printf("Name %s at %s declared at %s%n", name, ast_name.location(), decl.location());
			seman.declaredAt.put(ast_name, decl);
			return decl;
		} catch (SymbTable.CannotFndNameException e) {
			throw new Report.Error(ast_name.location(), String.format("Undefined symbol %s", name));
		}
	}

	@Override
	public Object visit(AstTrees<? extends AstTree> trees, Object arg) {
		System.out.printf("Visit %s%n", trees.title);
		switch (trees.title) {
			case "call parameters":
			case "block statements":
			case "declarations":
			case "function arguments":
				break;
			case "function declarations":
			case "type declarations":
			case "variable declarations": {
				symbols.newScope();
				for (var decl_ : trees) {
					var decl = (AstNameDecl)decl_;
					ins(decl.name, decl);
				}
				break;
			}
			case "record declarations": {
				break;
			}
			default:
				throw new Report.InternalError();
		}
		super.visit(trees, arg);
		System.out.printf("Leave %s%n", trees.title);
		return null;
	}

	@Override
	public Object visit(AstParDecl parDecl, Object arg) {
		ins(parDecl.name, parDecl);
		super.visit(parDecl, arg);
		return null;
	}

	@Override
	public Object visit(AstFunDecl funDecl, Object arg) {
		// Args
		symbols.newScope();
		
		super.visit(funDecl, arg);

		// Pop arguments
		symbols.oldScope();
		return null;
	}

	@Override
	public Object visit(AstDeclStmt declStmt, Object arg) {
		super.visit(declStmt, arg);

		// Pop declarations
		var decls = declStmt.decls;
		for (var i = 0; i < decls.size(); i++) symbols.oldScope();
		return null;
	}

	@Override
	public Object visit(AstNameType nameType, Object arg) {
		fnd(nameType, nameType.name);
		super.visit(nameType, arg);
		return null;
	}

	@Override
	public Object visit(AstNameExpr nameExpr, Object arg) {
		fnd(nameExpr, nameExpr.name);
		super.visit(nameExpr, arg);
		return null;
	}

	@Override
	public Object visit(AstCallExpr callExpr, Object arg) {
		fnd(callExpr, callExpr.name);
		super.visit(callExpr, arg);
		return null;
	}

	@Override
	public Object visit(AstRecExpr recExpr, Object arg) { return null; }

}
