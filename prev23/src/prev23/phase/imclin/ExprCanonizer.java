package prev23.phase.imclin;

import java.util.*;

import prev23.common.report.Report;
import prev23.data.mem.*;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.code.stmt.*;
import prev23.data.imc.visitor.*;

/**
 * Expression canonizer.
 */
public class ExprCanonizer implements ImcVisitor<ImcExpr, Vector<ImcStmt>> {
    boolean can_call;

    public ExprCanonizer(boolean can_call) {
        this.can_call = can_call;
    }
    @Override
    public ImcExpr visit(ImcBINOP binOp, Vector<ImcStmt> lin) {
        can_call = false;
        var lhs = binOp.fstExpr.accept(this, lin);
        var rhs = binOp.sndExpr.accept(this, lin);
        return new ImcBINOP(binOp.oper, lhs, rhs);
    }

    @Override
    public ImcExpr visit(ImcCALL call, Vector<ImcStmt> lin) {
        var this_can_cal = can_call;
        can_call = false;

        var lin_args = new Vector<ImcExpr>();
        for (var arg : call.args) {
            lin_args.add(arg.accept(this, lin));
        }

        var lin_call = new ImcCALL(call.label, call.offs, lin_args);

        if (this_can_cal) return lin_call;

        var rv = new ImcTEMP(new MemTemp());

        lin.add(new ImcMOVE(rv, lin_call));

        return rv;
    }

    @Override
    public ImcExpr visit(ImcCONST constant, Vector<ImcStmt> lin) {
        return constant;
    }

    @Override
    public ImcExpr visit(ImcMEM mem, Vector<ImcStmt> lin) {
        can_call = false;
        return new ImcMEM(mem.addr.accept(this, lin));
    }

    @Override
    public ImcExpr visit(ImcNAME name, Vector<ImcStmt> lin) {
        return name;
    }

    @Override
    public ImcExpr visit(ImcTEMP temp, Vector<ImcStmt> lin) {
        return temp;
    }

    @Override
    public ImcExpr visit(ImcUNOP unOp, Vector<ImcStmt> lin) {
        can_call = false;
        return new ImcUNOP(unOp.oper, unOp.subExpr.accept(this, lin));
    }
}
