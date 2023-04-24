package prev23.phase.asmgen;

import java.util.*;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.code.stmt.*;
import prev23.data.imc.visitor.*;
import prev23.data.mem.*;
import prev23.data.asm.*;
import prev23.common.report.*;

/**
 * Machine code generator for statements.
 */
public class StmtGenerator implements ImcVisitor<Vector<AsmInstr>, Object> {
    @Override
    public Vector<AsmInstr> visit(ImcCJUMP cjump, Object arg) {
        var negated_condition = false;
        var condition_expr = cjump.cond;

        if (condition_expr instanceof ImcUNOP unop && unop.oper == ImcUNOP.Oper.NOT) {
            negated_condition = true;
            condition_expr = unop.subExpr;
        }

        var instructions = new Vector<AsmInstr>();

        var condition = condition_expr.accept(new ExprGenerator(), instructions);

        instructions.add(
            new AsmOPER(
                String.format("\t\t%s\t`s0, %s", negated_condition ? "BNZ" : "BZ", cjump.posLabel.name),
                new Vector<>(List.of(condition)),
                new Vector<>(),
                new Vector<>(List.of(cjump.posLabel, cjump.negLabel))
            )
        );

        return instructions;
    }

    @Override
    public Vector<AsmInstr> visit(ImcESTMT eStmt, Object arg) {
        var instructions = new Vector<AsmInstr>();
        eStmt.expr.accept(new ExprGenerator(), instructions);
        return instructions;
    }

    @Override
    public Vector<AsmInstr> visit(ImcJUMP jump, Object arg) {
        return new Vector<>(List.of(
            new AsmOPER(
                    String.format("\t\tJMP\t%s", jump.label.name),
                    new Vector<>(),
                    new Vector<>(),
                    new Vector<>(List.of(jump.label))
            )
        ));
    }

    @Override
    public Vector<AsmInstr> visit(ImcLABEL label, Object arg) {
        return new Vector<>(List.of(new AsmLABEL(label.label)));
    }

    @Override
    public Vector<AsmInstr> visit(ImcMOVE move, Object arg) {
        var instructions = new Vector<AsmInstr>();

        var dst = (move.dst instanceof ImcMEM mem ? mem.addr : move.dst)
                .accept(new ExprGenerator(), instructions);
        var src = move.src.accept(new ExprGenerator(), instructions);

        instructions.add(move.dst instanceof ImcMEM mem
                ? new AsmOPER(
                        "\t\tSTO\t`s0, `s1, #0",
                        new Vector<>(List.of(src,dst)),
                        new Vector<>(),
                        new Vector<>()
                )
                : new AsmMOVE(
                        "\t\tSET\t`d0, `s0",
                        new Vector<>(List.of(src)),
                        new Vector<>(List.of(dst))
                )
        );

        return instructions;
    }

    @Override
    public Vector<AsmInstr> visit(ImcSTMTS stmts, Object arg) {
        var instructions = new Vector<AsmInstr>();

        for (var stmt : stmts.stmts) {
            instructions.addAll(stmt.accept(this, arg));
        }

        return instructions;
    }
}
