package prev23.phase.imcgen;

import prev23.common.report.Locatable;
import prev23.common.report.Report;
import prev23.data.ast.tree.AstTree;
import prev23.data.ast.tree.AstTrees;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.visitor.AstFullVisitor;
import prev23.data.imc.code.ImcInstr;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.code.stmt.*;
import prev23.data.mem.*;
import prev23.data.typ.SemArr;
import prev23.data.typ.SemChar;
import prev23.phase.memory.Memory;
import prev23.phase.seman.SemAn;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class CodeGenerator extends AstFullVisitor<ImcInstr, Boolean> {
    private final Stack<MemFrame> frame_stack = new Stack<>();

    private static <T> T expect(Object o, Class<T> expected_class, Locatable ast) {
        if (expected_class.isInstance(o)) return expected_class.cast(o);

        var error = String.format("Unexpected class '%s', expected '%s'",
                (o != null) ? o.getClass().getSimpleName() : "null", expected_class.getSimpleName());
        if (ast != null) throw new Report.Error(ast, error);
        if (o instanceof Locatable l) throw new Report.Error(l, error);
        throw new Report.Error(error);
    }

    private <T extends ImcInstr> T accept_and_expect(AstTree ast, Class<T> type) {
        return expect(ast.accept(this, null), type, ast);
    }

    private <T extends ImcInstr> T accept_and_expect(AstTree ast, Class<T> type, Boolean is_return) {
        return expect(ast.accept(this, is_return), type, ast);
    }

    private ImcStmt accept_stmt(AstTree ast, boolean is_return) {
        return accept_and_expect(ast, ImcStmt.class, is_return);
    }

    private ImcExpr accept_expr(AstTree ast) {
        return accept_and_expect(ast, ImcExpr.class);
    }

    private ImcExpr accept_mem_addr(AstTree ast) {
        return expect(accept_expr(ast), ImcMEM.class, ast).addr;
    }

    private static <T extends AstNameDecl> T get_declared_at(AstNameExpr expr, Class<T> type) {
        return expect(SemAn.declaredAt.get(expr), type, expr);
    }

    private static AstFunDecl get_function(AstNameExpr expr) {
        return get_declared_at(expr, AstFunDecl.class);
    }

    private static MemFrame get_frame(AstNameExpr expr) {
        return Memory.frames.get(get_function(expr));
    }

    private static MemRelAccess get_rel_access(AstMemDecl decl) {
        return get_rel_access(decl, null);
    }

    private static MemRelAccess get_rel_access(AstMemDecl decl, AstTree ast) {
        return expect(Memory.accesses.get(decl), MemRelAccess.class, (ast == null) ? decl : ast);
    }

    private static MemRelAccess get_rel_access(AstNameExpr expr) {
        return get_rel_access(get_declared_at(expr, AstMemDecl.class), expr);
    }

    private static ImcInstr declare_expr(AstExpr ast, ImcExpr e) {
        ImcGen.exprImc.put(ast, e);
        return e;
    }

    private ImcInstr declare_stmt(AstStmt ast, ImcStmt s, boolean is_return) {
        var ret_s = s;
        if (is_return) {
            if (s instanceof ImcESTMT expr) {
                ret_s = new ImcMOVE(new ImcTEMP(frame_stack.peek().RV), expr.expr);
            } else {
                ret_s = new ImcSTMTS(new Vector<>(List.of(
                    s, new ImcMOVE(new ImcTEMP(frame_stack.peek().RV), new ImcCONST(0))
                )));
            }
        }
        ImcGen.stmtImc.put(ast, ret_s);
        return ret_s;
    }

    @Override
    public ImcInstr visit(AstArrExpr arrExpr, Boolean is_return) {
        var arr = accept_mem_addr(arrExpr.arr);
        var idx = accept_expr(arrExpr.idx);
        var element_size = ((SemArr) SemAn.ofType.get(arrExpr.arr).actualType()).elemType.size();
        return declare_expr(arrExpr, new ImcMEM(
                new ImcBINOP(ImcBINOP.Oper.ADD, arr, new ImcBINOP(
                        ImcBINOP.Oper.MUL, idx, new ImcCONST(element_size)))));
    }

    @Override
    public ImcInstr visit(AstAtomExpr atomExpr, Boolean is_return) {
        return declare_expr(atomExpr, switch (atomExpr.type) {
            case VOID, PTR -> new ImcCONST(0);
            case CHAR -> new ImcCONST(atomExpr.value.codePointAt(1));
            case INT -> new ImcCONST(Long.parseLong(atomExpr.value));
            case BOOL -> new ImcCONST(atomExpr.value.equals("true") ? 1 : 0);
            case STR -> new ImcNAME(Memory.strings.get(atomExpr).label);
        });
    }

    @Override
    public ImcInstr visit(AstBinExpr binExpr, Boolean is_return) {
        var lhs = accept_expr(binExpr.fstExpr);
        var rhs = accept_expr(binExpr.sndExpr);
        return declare_expr(binExpr, switch (binExpr.oper) {
            case OR -> new ImcBINOP(ImcBINOP.Oper.OR, lhs, rhs);
            case AND -> new ImcBINOP(ImcBINOP.Oper.AND, lhs, rhs);
            case EQU -> new ImcBINOP(ImcBINOP.Oper.EQU, lhs, rhs);
            case NEQ -> new ImcBINOP(ImcBINOP.Oper.NEQ, lhs, rhs);
            case LTH -> new ImcBINOP(ImcBINOP.Oper.LTH, lhs, rhs);
            case GTH -> new ImcBINOP(ImcBINOP.Oper.GTH, lhs, rhs);
            case LEQ -> new ImcBINOP(ImcBINOP.Oper.LEQ, lhs, rhs);
            case GEQ -> new ImcBINOP(ImcBINOP.Oper.GEQ, lhs, rhs);
            case ADD -> new ImcBINOP(ImcBINOP.Oper.ADD, lhs, rhs);
            case SUB -> new ImcBINOP(ImcBINOP.Oper.SUB, lhs, rhs);
            case MUL -> new ImcBINOP(ImcBINOP.Oper.MUL, lhs, rhs);
            case DIV -> new ImcBINOP(ImcBINOP.Oper.DIV, lhs, rhs);
            case MOD -> new ImcBINOP(ImcBINOP.Oper.MOD, lhs, rhs);
        });
    }

    @Override
    public ImcInstr visit(AstCallExpr callExpr, Boolean is_return) {
        Vector<Long> offsets = new Vector<>();
        Vector<ImcExpr> args = new Vector<>();

        var fun_decl = get_function(callExpr);
        var current_frame = frame_stack.peek();
        var call_frame = get_frame(callExpr);

        ImcExpr static_link = new ImcCONST(0);

        if (call_frame.depth != 1) {
            static_link = new ImcTEMP(current_frame.FP);

            var hops = current_frame.depth - call_frame.depth + 1;
            assert hops >= 0;

            for (int i = 0; i < hops; i++) static_link = new ImcMEM(static_link);
        }
        
        offsets.add(0L);
        args.add(static_link);

        for (var i = 0; i < fun_decl.pars.size(); i++) {
            offsets.add(get_rel_access(fun_decl.pars.get(i)).offset);
            args.add(accept_expr(callExpr.args.get(i)));
        }
        return declare_expr(callExpr, new ImcCALL(call_frame.label, offsets, args));
    }

    @Override
    public ImcInstr visit(AstCastExpr castExpr, Boolean is_return) {
        var cast_from = accept_expr(castExpr.expr);

        if (SemAn.isType.get(castExpr.type).actualType() instanceof SemChar) {
            return declare_expr(castExpr, new ImcBINOP(ImcBINOP.Oper.MOD, cast_from, new ImcCONST(256)));
        }
        return declare_expr(castExpr, cast_from);
    }

    @Override
    public ImcInstr visit(AstNewExpr newExpr, Boolean is_return) {
        var static_link = (frame_stack.empty()) ? new ImcCONST(0) : new ImcTEMP(frame_stack.peek().FP);
        return declare_expr(newExpr, new ImcCALL(
                new MemLabel("new"),
                new Vector<>(List.of(0L, 8L)),
                new Vector<>(List.of(static_link, new ImcCONST(SemAn.isType.get(newExpr.type).size())))
                ));
    }

    @Override
    public ImcInstr visit(AstDelExpr delExpr, Boolean is_return) {
        var static_link = (frame_stack.empty()) ? new ImcCONST(0) : new ImcTEMP(frame_stack.peek().FP);
        return declare_expr(delExpr, new ImcCALL(
                new MemLabel("del"),
                new Vector<>(List.of(0L, 8L)),
                new Vector<>(List.of(static_link, accept_expr(delExpr.expr)))
        ));
    }

    @Override
    public ImcInstr visit(AstFunDecl funDecl, Boolean is_return) {
        if (funDecl.stmt == null) return null;

        var current_frame = Memory.frames.get(funDecl);

        frame_stack.push(current_frame);
        accept_stmt(funDecl.stmt, true);
        frame_stack.pop();
        
        return null;
    }

    @Override
    public ImcInstr visit(AstNameExpr nameExpr, Boolean is_return) {
        var access = Memory.accesses.get((AstMemDecl) SemAn.declaredAt.get(nameExpr));
        if (access instanceof MemAbsAccess abs) {
            return declare_expr(nameExpr, new ImcMEM(new ImcNAME(abs.label)));
        } else if (access instanceof MemRelAccess rel) {
            var current_frame = frame_stack.peek();
            int hops = current_frame.depth - rel.depth;
            assert hops >= 0;

            ImcExpr frame_pointer = new ImcTEMP(current_frame.FP);

            for (int i = 0; i < hops; i++) frame_pointer = new ImcMEM(frame_pointer);

            return declare_expr(nameExpr, new ImcMEM(new ImcBINOP(ImcBINOP.Oper.ADD, frame_pointer, new ImcCONST(rel.offset))));
        } else throw new Report.InternalError();
    }

    @Override
    public ImcInstr visit(AstPfxExpr pfxExpr, Boolean is_return) {
        return declare_expr(pfxExpr, switch (pfxExpr.oper) {
            case ADD -> accept_expr(pfxExpr.expr);
            case SUB -> {
                if (pfxExpr.expr instanceof AstAtomExpr atom && atom.type == AstAtomExpr.Type.INT) {
                    yield new ImcCONST(Long.parseLong("-" + atom.value));
                } else {
                    yield new ImcUNOP(ImcUNOP.Oper.NEG, accept_expr(pfxExpr.expr));
                }
            }
            case NOT -> new ImcUNOP(ImcUNOP.Oper.NOT, accept_expr(pfxExpr.expr));
            case PTR -> expect(accept_expr(pfxExpr.expr), ImcMEM.class, pfxExpr).addr;
        });
    }

    @Override
    public ImcInstr visit(AstRecExpr recExpr, Boolean is_return) {
        var rec = accept_mem_addr(recExpr.rec);
        var rel = (MemRelAccess)Memory.accesses.get((AstMemDecl) SemAn.declaredAt.get(recExpr.comp));
        return declare_expr(recExpr, new ImcMEM(new ImcBINOP(ImcBINOP.Oper.ADD, rec, new ImcCONST(rel.offset))));
    }

    @Override
    public ImcInstr visit(AstSfxExpr sfxExpr, Boolean is_return) {
        return declare_expr(sfxExpr, switch (sfxExpr.oper) {
            case PTR -> new ImcMEM(accept_expr(sfxExpr.expr));
        });
    }


    // Statements
    @Override
    public ImcInstr visit(AstAssignStmt assignStmt, Boolean is_return) {
        return declare_stmt(assignStmt, new ImcMOVE(accept_expr(assignStmt.dst), accept_expr(assignStmt.src)), is_return);
    }

    @Override
    public ImcInstr visit(AstDeclStmt declStmt, Boolean is_return) {
        declStmt.decls.accept(this, false);

        return declare_stmt(declStmt, accept_stmt(declStmt.stmt, is_return), false);
    }

    @Override
    public ImcInstr visit(AstExprStmt exprStmt, Boolean is_return) {
        return declare_stmt(exprStmt, new ImcESTMT(accept_expr(exprStmt.expr)), is_return);
    }

    @Override
    public ImcInstr visit(AstIfStmt ifStmt, Boolean is_return) {
        var start_label = new MemLabel();
        var positive_label = new MemLabel();
        var negative_label =  new MemLabel();
        var end_label = new MemLabel();

        var stmts = new Vector<>(List.of(
            new ImcJUMP(start_label),
            new ImcLABEL(start_label),
            new ImcCJUMP(accept_expr(ifStmt.cond), positive_label, negative_label),
            new ImcLABEL(positive_label),
            accept_stmt(ifStmt.thenStmt, false),
            new ImcJUMP(end_label),
            new ImcLABEL(negative_label)
        ));

        if (ifStmt.elseStmt != null) stmts.add(
           accept_stmt(ifStmt.elseStmt, false)
        );

        stmts.addAll(List.of(
            new ImcJUMP(end_label),
            new ImcLABEL(end_label)
        ));

        return declare_stmt(ifStmt, new ImcSTMTS(stmts), is_return);
    }

    @Override
    public ImcInstr visit(AstStmts stmts, Boolean is_return) {
        var v_stmts = new Vector<ImcStmt>();
        int i = stmts.stmts.size();
        for (var stmt : stmts.stmts) {
            v_stmts.add(accept_stmt(stmt, is_return && --i == 0));
        }
        return declare_stmt(stmts, new ImcSTMTS(v_stmts), false);
    }

    @Override
    public ImcInstr visit(AstWhileStmt whileStmt, Boolean is_return) {
        var start_label = new MemLabel();
        var condition_label = new MemLabel();
        var body_label = new MemLabel();
        var end_label = new MemLabel();

        return declare_stmt(whileStmt, new ImcSTMTS(new Vector<>(List.of(
                new ImcJUMP(start_label),
                new ImcLABEL(start_label),
                new ImcJUMP(condition_label),
                new ImcLABEL(condition_label),
                new ImcCJUMP(new ImcUNOP(ImcUNOP.Oper.NOT, accept_expr(whileStmt.cond)), end_label, body_label),
                new ImcLABEL(body_label),
                accept_stmt(whileStmt.bodyStmt, false),
                new ImcJUMP(condition_label),
                new ImcLABEL(end_label)
        ))), is_return);
    }
}
