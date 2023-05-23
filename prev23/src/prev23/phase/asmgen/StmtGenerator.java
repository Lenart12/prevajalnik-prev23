package prev23.phase.asmgen;

import java.util.*;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.code.stmt.*;
import prev23.data.imc.visitor.*;
import prev23.data.lin.LinCodeChunk;
import prev23.data.mem.*;
import prev23.data.asm.*;
import prev23.common.report.*;

/**
 * Machine code generator for statements.
 */
public class StmtGenerator implements ImcVisitor<Vector<AsmInstr>, LinCodeChunk> {
    private boolean visit_binop_cjump(ImcCJUMP cjump, Vector<AsmInstr> instructions) {
        if (!(cjump.cond instanceof ImcBINOP binop)) return false;

        switch (binop.oper) {
            case EQU,NEQ,LTH,GTH,LEQ,GEQ: break;
            default: return false;
        }

        ImcExpr lhs = null;
        long value = -1;
        boolean flipped = false;
        var is_const = true;
        var dst = new MemTemp();

        if (binop.sndExpr instanceof ImcCONST cons
                && cons.value >= 0 && cons.value < 256) {
            lhs = binop.fstExpr;
            value = cons.value;
        } else if (binop.fstExpr instanceof ImcCONST cons
                && cons.value >= 0 && cons.value < 256) {
            lhs = binop.sndExpr;
            value = cons.value;
            flipped = true;
        } else {
            is_const = false;
        }

        if (is_const) {
            var lhs_temp = lhs.accept(new ExprGenerator(), instructions);

            instructions.add(new AsmOPER(
                    String.format("\t\tCMP\t`d0,`s0,#%x", value),
                    new Vector<>(List.of(lhs_temp)),
                    new Vector<>(List.of(dst)),
                    new Vector<>()
            ));
        } else {
            var lhs_temp = binop.fstExpr.accept(new ExprGenerator(), instructions);
            var rhs_temp = binop.sndExpr.accept(new ExprGenerator(), instructions);

            instructions.add(new AsmOPER(
                    "\t\tCMP\t`d0,`s0,`s1",
                    new Vector<>(List.of(lhs_temp, rhs_temp)),
                    new Vector<>(List.of(dst)),
                    new Vector<>()
            ));
        }

        instructions.add(new AsmOPER(
                String.format("\t\tB%s\t`s0,%s", switch (binop.oper) {
                    case EQU -> "Z";
                    case NEQ -> "NZ";
                    case LTH -> flipped ? "P" : "N";
                    case GTH -> flipped ? "N" : "P";
                    case LEQ -> flipped ? "NN" : "NP";
                    case GEQ -> flipped ? "NP" : "NN";
                    default -> throw new IllegalStateException("Unexpected value: " + binop.oper);
                }, cjump.posLabel.name),
                new Vector<>(List.of(dst)),
                new Vector<>(),
                new Vector<>(List.of(cjump.posLabel, cjump.negLabel))
        ));

        return true;
    }


    @Override
    public Vector<AsmInstr> visit(ImcCJUMP cjump, LinCodeChunk chunk) {
        var instructions = new Vector<AsmInstr>();

        if (visit_binop_cjump(cjump, instructions)) return instructions;

        var negated_condition = false;
        var condition_expr = cjump.cond;

        if (condition_expr instanceof ImcUNOP unop && unop.oper == ImcUNOP.Oper.NOT) {
            negated_condition = true;
            condition_expr = unop.subExpr;

            if (condition_expr instanceof ImcBINOP binop
                    && binop.oper != ImcBINOP.Oper.OR
                    && binop.oper != ImcBINOP.Oper.AND) {
                var inv_condition = new ImcBINOP(
                        switch (binop.oper) {
                            case EQU -> ImcBINOP.Oper.NEQ;
                            case NEQ -> ImcBINOP.Oper.EQU;
                            case LTH -> ImcBINOP.Oper.GEQ;
                            case GTH -> ImcBINOP.Oper.LEQ;
                            case LEQ -> ImcBINOP.Oper.GTH;
                            case GEQ -> ImcBINOP.Oper.LTH;
                            default -> throw new Report.InternalError();
                        },
                        binop.fstExpr, binop.sndExpr
                ) ;
                var inv_cjump = new ImcCJUMP(inv_condition, cjump.posLabel, cjump.negLabel);
                visit_binop_cjump(inv_cjump, instructions);
                return instructions;
            }
        }

        var condition = condition_expr.accept(new ExprGenerator(), instructions);

        instructions.add(
            new AsmOPER(
                String.format("\t\t%s\t`s0,%s", negated_condition ? "BZ" : "BNZ", cjump.posLabel.name),
                new Vector<>(List.of(condition)),
                new Vector<>(),
                new Vector<>(List.of(cjump.posLabel, cjump.negLabel))
            )
        );

        return instructions;
    }

    @Override
    public Vector<AsmInstr> visit(ImcESTMT eStmt, LinCodeChunk chunk) {
        var instructions = new Vector<AsmInstr>();
        eStmt.expr.accept(new ExprGenerator(), instructions);
        return instructions;
    }

    @Override
    public Vector<AsmInstr> visit(ImcJUMP jump, LinCodeChunk chunk) {
        // Jump to exit must include RV so that it is not
        // spilled in regall
        Vector<MemTemp> uses = new Vector<>();
        if (chunk != null) {
            if (jump.label == chunk.exitLabel) {
                uses.add(chunk.frame.RV);
            }
        } else {
            Report.warning("StmtGenerator generating for ImcJUMP without chunk info");
        }

        return new Vector<>(List.of(
            new AsmOPER(
                    String.format("\t\tJMP\t%s", jump.label.name),
                    uses,
                    new Vector<>(),
                    new Vector<>(List.of(jump.label))
            )
        ));
    }

    @Override
    public Vector<AsmInstr> visit(ImcLABEL label, LinCodeChunk chunk) {
        return new Vector<>(List.of(new AsmLABEL(label.label)));
    }


    private boolean visit_store_const(ImcBINOP binop, ImcExpr src, Vector<AsmInstr> instructions) {
        ImcExpr lhs;
        long value;

        if (binop.sndExpr instanceof ImcCONST imc_const &&
                imc_const.value < 256 && imc_const.value >= 0) {
            lhs = binop.fstExpr;
            value = imc_const.value;
        } else if (binop.fstExpr instanceof ImcCONST imc_const &&
                imc_const.value < 256 && imc_const.value >= 0) {
            lhs = binop.sndExpr;
            value = imc_const.value;
        } else {
            return false;
        }

        var lhs_temp = lhs.accept(new ExprGenerator(), instructions);
        var src_temp = src.accept(new ExprGenerator(), instructions);

        instructions.add(new AsmOPER(
                String.format("\t\tSTO\t`s0,`s1,#%x", value),
                new Vector<>(List.of(src_temp, lhs_temp)),
                new Vector<>(),
                new Vector<>()
        ));
        return true;
    }

    @Override
    public Vector<AsmInstr> visit(ImcMOVE move, LinCodeChunk chunk) {
        var instructions = new Vector<AsmInstr>();

        if (move.dst instanceof ImcMEM mem) {
            if (mem.addr instanceof ImcBINOP binop && binop.oper == ImcBINOP.Oper.ADD) {
                if (visit_store_const(binop, move.src, instructions)) return instructions;

                var lhs_result = binop.fstExpr.accept(new ExprGenerator(), instructions);
                var rhs_result = binop.sndExpr.accept(new ExprGenerator(), instructions);

                var src_temp = move.src.accept(new ExprGenerator(), instructions);

                instructions.add(new AsmOPER(
                        "\t\tSTO\t`s0,`s1,`s2",
                        new Vector<>(List.of(src_temp, lhs_result, rhs_result)),
                        new Vector<>(),
                        new Vector<>()
                ));
            } else {
                var dst = mem.addr.accept(new ExprGenerator(), instructions);
                var src = move.src.accept(new ExprGenerator(), instructions);
                instructions.add(
                        new AsmOPER(
                        "\t\tSTO\t`s0,`s1,#0",
                        new Vector<>(List.of(src,dst)),
                        new Vector<>(),
                        new Vector<>()
                ));
            }

        } else {
            var dst = move.dst.accept(new ExprGenerator(), instructions);
            var src = move.src.accept(new ExprGenerator(), instructions);

            instructions.add(
                    new AsmMOVE(
                    "\t\tSET\t`d0,`s0",
                    new Vector<>(List.of(src)),
                    new Vector<>(List.of(dst))
            ));
        }

        return instructions;
    }

    @Override
    public Vector<AsmInstr> visit(ImcSTMTS stmts, LinCodeChunk chunk) {
        var instructions = new Vector<AsmInstr>();

        for (var stmt : stmts.stmts) {
            instructions.addAll(stmt.accept(this, chunk));
        }

        return instructions;
    }
}
