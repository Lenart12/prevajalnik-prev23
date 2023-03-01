package prev23.phase.abstr;

import java.util.*;

import prev23.common.logger.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.*;

/**
 * Logs an abstract syntax tree.
 */
public class AbsLogger implements AstVisitor<Object, Object> {

	/** A list of subvisitors for logging Objects of the subsequent phases. */
	private final LinkedList<AstVisitor<Object, ?>> subvisitors;

	/** The logger the log should be written to. */
	private final Logger logger;

	/**
	 * Construct a new visitor with a logger the log should be written to.
	 * 
	 * @param logger The logger the log should be written to.
	 */
	public AbsLogger(Logger logger) {
		this.logger = logger;
		this.subvisitors = new LinkedList<AstVisitor<Object, ?>>();
	}

	/**
	 * Adds a new subvisitor to this visitor.
	 * 
	 * @param subvisitor The subvisitor.
	 * @return This visitor.
	 */
	public AbsLogger addSubvisitor(AstVisitor<Object, ?> subvisitor) {
		subvisitors.addLast(subvisitor);
		return this;
	}

	// GENERAL PURPOSE

	@Override
	public Object visit(AstTrees<? extends AstTree> trees, Object arg) {
		if (logger == null)
			return null;
		if (trees.size() == 0)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(trees.id()));
		logger.addAttribute("label", "{" + trees.title + "}");
		for (AstTree t : trees)
			if (t != null)
				t.accept(this, "");
		if (trees.location() != null)
			trees.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			trees.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	// DECLARATIONS

	@Override
	public Object visit(AstCmpDecl compDecl, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(compDecl.id()));
		logger.addAttribute("label", "CompDecl");
		logger.addAttribute("lexeme", compDecl.name);
		if (compDecl.type != null)
			compDecl.type.accept(this, "");
		compDecl.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			compDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstFunDecl funDecl, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(funDecl.id()));
		logger.addAttribute("label", "FunDecl");
		logger.addAttribute("lexeme", funDecl.name);
		if (funDecl.pars != null)
			funDecl.pars.accept(this, "Pars");
		if (funDecl.type != null)
			funDecl.type.accept(this, "");
		if (funDecl.stmt != null)
			funDecl.stmt.accept(this, "");
		funDecl.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			funDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstParDecl parDecl, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(parDecl.id()));
		logger.addAttribute("label", "ParDecl");
		logger.addAttribute("lexeme", parDecl.name);
		if (parDecl.type != null)
			parDecl.type.accept(this, "");
		parDecl.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			parDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstTypDecl typeDecl, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(typeDecl.id()));
		logger.addAttribute("label", "TypeDecl");
		logger.addAttribute("lexeme", typeDecl.name);
		if (typeDecl.type != null)
			typeDecl.type.accept(this, "");
		typeDecl.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			typeDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstVarDecl varDecl, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(varDecl.id()));
		logger.addAttribute("label", "VarDecl");
		logger.addAttribute("lexeme", varDecl.name);
		if (varDecl.type != null)
			varDecl.type.accept(this, "");
		varDecl.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			varDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	// EXPRESSIONS

	@Override
	public Object visit(AstArrExpr arrExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(arrExpr.id()));
		logger.addAttribute("label", "ArrExpr");
		if (arrExpr.arr != null)
			arrExpr.arr.accept(this, "");
		if (arrExpr.idx != null)
			arrExpr.idx.accept(this, "");
		arrExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			arrExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstAtomExpr atomExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(atomExpr.id()));
		logger.addAttribute("label", "AtomExpr");
		if (atomExpr.type != null)
			logger.addAttribute("spec", atomExpr.type.toString());
		if (atomExpr.value != null)
			logger.addAttribute("lexeme", atomExpr.value);
		atomExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			atomExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstBinExpr binExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(binExpr.id()));
		logger.addAttribute("label", "BinExpr");
		if (binExpr.oper != null)
			logger.addAttribute("spec", binExpr.oper.toString());
		if (binExpr.fstExpr != null)
			binExpr.fstExpr.accept(this, "");
		if (binExpr.sndExpr != null)
			binExpr.sndExpr.accept(this, "");
		binExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			binExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstCallExpr callExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(callExpr.id()));
		logger.addAttribute("label", "CallExpr");
		if (callExpr.name != null)
			logger.addAttribute("lexeme", callExpr.name);
		if ((callExpr.args != null) && (callExpr.args.size() > 0))
			callExpr.args.accept(this, "CallArgs");
		callExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			callExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstCastExpr castExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(castExpr.id()));
		logger.addAttribute("label", "CastExpr");
		if (castExpr.expr != null)
			castExpr.expr.accept(this, "");
		if (castExpr.type != null)
			castExpr.type.accept(this, "");
		castExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			castExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstDelExpr delExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(delExpr.id()));
		logger.addAttribute("label", "DelExpr");
		if (delExpr.expr != null)
			delExpr.expr.accept(this, "");
		delExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			delExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstNameExpr nameExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(nameExpr.id()));
		logger.addAttribute("label", "NameExpr");
		if (nameExpr.name != null)
			logger.addAttribute("lexeme", nameExpr.name);
		nameExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			nameExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstNewExpr newExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(newExpr.id()));
		logger.addAttribute("label", "NewExpr");
		if (newExpr.type != null)
			newExpr.type.accept(this, "");
		newExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			newExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstPfxExpr pfxExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(pfxExpr.id()));
		logger.addAttribute("label", "PfxExpr");
		if (pfxExpr.oper != null)
			logger.addAttribute("spec", pfxExpr.oper.toString());
		if (pfxExpr.expr != null)
			pfxExpr.expr.accept(this, "");
		pfxExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			pfxExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstRecExpr recExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(recExpr.id()));
		logger.addAttribute("label", "RecExpr");
		if (recExpr.rec != null)
			recExpr.rec.accept(this, "");
		if (recExpr.comp != null)
			recExpr.comp.accept(this, "");
		recExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			recExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstSfxExpr sfxExpr, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(sfxExpr.id()));
		logger.addAttribute("label", "SfxExpr");
		if (sfxExpr.oper != null)
			logger.addAttribute("spec", sfxExpr.oper.toString());
		if (sfxExpr.expr != null)
			sfxExpr.expr.accept(this, "");
		sfxExpr.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			sfxExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	// STATEMENTS

	@Override
	public Object visit(AstAssignStmt assignStmt, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(assignStmt.id()));
		logger.addAttribute("label", "AssignStmt");
		if (assignStmt.dst != null)
			assignStmt.dst.accept(this, "");
		if (assignStmt.src != null)
			assignStmt.src.accept(this, "");
		assignStmt.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			assignStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstExprStmt exprStmt, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(exprStmt.id()));
		logger.addAttribute("label", "ExprStmt");
		if (exprStmt.expr != null)
			exprStmt.expr.accept(this, "");
		exprStmt.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			exprStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstDeclStmt declStmt, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(declStmt.id()));
		logger.addAttribute("label", "DeclStmt");
		if (declStmt.decls != null)
			declStmt.decls.accept(this, "");
		if (declStmt.stmt != null)
			declStmt.stmt.accept(this, "");
		declStmt.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			declStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstIfStmt ifStmt, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(ifStmt.id()));
		logger.addAttribute("label", "IfStmt");
		if (ifStmt.cond != null)
			ifStmt.cond.accept(this, "");
		if (ifStmt.thenStmt != null)
			ifStmt.thenStmt.accept(this, "ThenStmt");
		if (ifStmt.elseStmt != null)
			ifStmt.elseStmt.accept(this, "ElseStmt");
		ifStmt.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			ifStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstStmts stmts, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(stmts.id()));
		logger.addAttribute("label", "Stmts");
		if (stmts != null)
			stmts.stmts.accept(this, "");
		stmts.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			stmts.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstWhileStmt whileStmt, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(whileStmt.id()));
		logger.addAttribute("label", "WhileStmt");
		if (whileStmt.cond != null)
			whileStmt.cond.accept(this, "");
		if (whileStmt.bodyStmt != null)
			whileStmt.bodyStmt.accept(this, "BodyStmt");
		whileStmt.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			whileStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	// TYPES

	@Override
	public Object visit(AstArrType arrType, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(arrType.id()));
		logger.addAttribute("label", "ArrType");
		if (arrType.numElems != null)
			arrType.numElems.accept(this, "");
		if (arrType.elemType != null)
			arrType.elemType.accept(this, "");
		arrType.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			arrType.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstAtomType atomType, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(atomType.id()));
		logger.addAttribute("label", "AtomType");
		if (atomType.type != null)
			logger.addAttribute("spec", atomType.type.toString());
		atomType.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			atomType.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstNameType typeName, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(typeName.id()));
		logger.addAttribute("label", "NameType");
		if (typeName.name != null)
			logger.addAttribute("lexeme", typeName.name);
		typeName.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			typeName.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstPtrType ptrType, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(ptrType.id()));
		logger.addAttribute("label", "PtrType");
		if (ptrType.baseType != null)
			ptrType.baseType.accept(this, "");
		ptrType.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			ptrType.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AstRecType recType, Object arg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("id", Integer.toString(recType.id()));
		logger.addAttribute("label", "RecType");
		if (recType.comps != null)
			recType.comps.accept(this, "Comps");
		recType.location().log(logger);
		for (AstVisitor<Object, ?> subvisitor : subvisitors) {
			recType.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

}
