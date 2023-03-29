package prev23.phase.imcgen;

import prev23.data.ast.tree.AstTree;
import prev23.data.ast.tree.AstTrees;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.AstFullVisitor;

public class CodeGenerator extends AstFullVisitor<Object, Object> {
    @Override
    public Object visit(AstTrees<? extends AstTree> trees, Object arg) {
        return super.visit(trees, arg);
    }

    @Override
    public Object visit(AstArrExpr arrExpr, Object arg) {
        return super.visit(arrExpr, arg);
    }

    @Override
    public Object visit(AstAtomExpr atomExpr, Object arg) {
        return super.visit(atomExpr, arg);
    }

    @Override
    public Object visit(AstBinExpr binExpr, Object arg) {
        return super.visit(binExpr, arg);
    }

    @Override
    public Object visit(AstCallExpr callExpr, Object arg) {
        return super.visit(callExpr, arg);
    }

    @Override
    public Object visit(AstCastExpr castExpr, Object arg) {
        return super.visit(castExpr, arg);
    }

    @Override
    public Object visit(AstDelExpr delExpr, Object arg) {
        return super.visit(delExpr, arg);
    }

    @Override
    public Object visit(AstNameExpr nameExpr, Object arg) {
        return super.visit(nameExpr, arg);
    }

    @Override
    public Object visit(AstNewExpr newExpr, Object arg) {
        return super.visit(newExpr, arg);
    }

    @Override
    public Object visit(AstPfxExpr pfxExpr, Object arg) {
        return super.visit(pfxExpr, arg);
    }

    @Override
    public Object visit(AstRecExpr recExpr, Object arg) {
        return super.visit(recExpr, arg);
    }

    @Override
    public Object visit(AstSfxExpr sfxExpr, Object arg) {
        return super.visit(sfxExpr, arg);
    }


    // Statements
    @Override
    public Object visit(AstAssignStmt assignStmt, Object arg) {
        return super.visit(assignStmt, arg);
    }

    @Override
    public Object visit(AstDeclStmt declStmt, Object arg) {
        return super.visit(declStmt, arg);
    }

    @Override
    public Object visit(AstExprStmt exprStmt, Object arg) {
        return super.visit(exprStmt, arg);
    }

    @Override
    public Object visit(AstIfStmt ifStmt, Object arg) {
        return super.visit(ifStmt, arg);
    }

    @Override
    public Object visit(AstStmts stmts, Object arg) {
        return super.visit(stmts, arg);
    }

    @Override
    public Object visit(AstWhileStmt whileStmt, Object arg) {
        return super.visit(whileStmt, arg);
    }
}
