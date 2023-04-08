package prev23.phase.imclin;

import java.util.*;

import prev23.common.report.*;
import prev23.data.mem.*;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.code.stmt.*;
import prev23.data.imc.visitor.*;

/**
 * Statement canonizer.
 */
public class StmtCanonizer implements ImcVisitor<Vector<ImcStmt>, Object> {

    @Override
    public Vector<ImcStmt> visit(ImcCJUMP cjump, Object arg) {
        var lin = new Vector<ImcStmt>();
        lin.add(new ImcCJUMP(
                cjump.cond.accept(new ExprCanonizer(false), lin),
                cjump.posLabel,
                cjump.negLabel
        ));
        return lin;
    }

    @Override
    public Vector<ImcStmt> visit(ImcESTMT eStmt, Object arg) {
        var lin = new Vector<ImcStmt>();
        lin.add(new ImcESTMT(eStmt.expr.accept(new ExprCanonizer(true), lin)));
        return lin;
    }

    @Override
    public Vector<ImcStmt> visit(ImcJUMP jump, Object arg) {
        return new Vector<>(List.of(jump));
    }

    @Override
    public Vector<ImcStmt> visit(ImcLABEL label, Object arg) {
        return new Vector<>(List.of(label));
    }

    @Override
    public Vector<ImcStmt> visit(ImcMOVE move, Object arg) {
        var lin = new Vector<ImcStmt>();
        var dst = move.dst.accept(new ExprCanonizer(false), lin);
        var src = move.src.accept(new ExprCanonizer(true), lin);

        lin.add(new ImcMOVE(dst, src));
        return lin;
    }

    @Override
    public Vector<ImcStmt> visit(ImcSTMTS stmts, Object arg) {
        var lin = new Vector<ImcStmt>();
        for (var imc : stmts.stmts) {
            lin.addAll(imc.accept(this, arg));
        }
        return lin;
    }
}
