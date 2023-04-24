package prev23.phase.asmgen;

import java.util.*;

import prev23.common.report.Report;
import prev23.data.imc.code.ImcInstr;
import prev23.data.mem.*;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.visitor.*;
import prev23.data.asm.*;

/**
 * Machine code generator for expressions.
 */
public class ExprGenerator implements ImcVisitor<MemTemp, Vector<AsmInstr>> {

    private interface Tile {

        class MatchingResult {
            static final long INVALID = Long.MAX_VALUE;

            public MatchingResult(long cost, MemTemp temp, Vector<AsmInstr> instructions) {
                this.cost = cost;
                this.temp = temp;
                this.instructions = instructions;
            }

            public MatchingResult(long cost, MemTemp temp, AsmInstr instruction) {
                this(cost, temp, new Vector<>(List.of(instruction)));
            }

            public MatchingResult(long cost, MemTemp temp, Vector<AsmInstr> previous_instructions, AsmInstr instruction) {
                this(cost, temp, new Vector<>(previous_instructions));
                instructions.add(instruction);
            }

            long cost;
            MemTemp temp;
            Vector<AsmInstr> instructions;
            boolean is_valid() { return cost != INVALID; }
        }
        static MatchingResult Invalid() {
            return new MatchingResult(MatchingResult.INVALID, null, (Vector<AsmInstr>) null);
        }

        MatchingResult try_match(ImcExpr expr);
    }

    private final Vector<Tile> tiles = new Vector<>(
            List.of(
                    // Temps
                    new Tile() {
                        @Override
                        public MatchingResult try_match(ImcExpr expr) {
                            if (!(expr instanceof ImcTEMP temp)) return Tile.Invalid();
                            return new MatchingResult(0, temp.temp, new Vector<>());
                        }
                    },
                    // Addresses
                    new Tile() {
                        @Override
                        public MatchingResult try_match(ImcExpr expr) {
                            if (!(expr instanceof ImcNAME imc_name)) return Tile.Invalid();
                            var name = imc_name.label.name;

                            var dst = new MemTemp();
                            return new MatchingResult(
                                    1, dst, new AsmOPER(
                                    String.format("\t\tLDA\t`d0,%s", name),
                                    new Vector<>(),
                                    new Vector<>(List.of(dst)),
                                    new Vector<>()
                            )
                            );
                        }
                    },
                    // Memory
                    new Tile() {
                        @Override
                        public MatchingResult try_match(ImcExpr expr) {
                            if (!(expr instanceof ImcMEM mem)) return Tile.Invalid();
                            var src_result = match_any_tile(mem.addr);
                            if (!src_result.is_valid()) return Tile.Invalid();

                            var dst = new MemTemp();

                            return new MatchingResult(
                                    src_result.cost + 10, dst, src_result.instructions, new AsmOPER(
                                    "\t\tLDO\t`d0,`s0,0",
                                    new Vector<>(List.of(src_result.temp)),
                                    new Vector<>(List.of(dst)),
                                    new Vector<>()
                            )
                            );
                        }
                    },
                    // ADDU
                    new Tile() {
                        @Override
                        public MatchingResult try_match(ImcExpr expr) {
                            if (!(expr instanceof ImcBINOP binop) || binop.oper != ImcBINOP.Oper.MUL) return Tile.Invalid();

                            ImcCONST imc_const = null;
                            ImcExpr imc_other = null;

                            if (binop.fstExpr instanceof ImcCONST _const) {
                                imc_const = _const;
                                imc_other = binop.sndExpr;
                            } else if (binop.sndExpr instanceof ImcCONST _const) {
                                imc_const = _const;
                                imc_other = binop.fstExpr;
                            } else {
                                return Tile.Invalid();
                            }

                            if (imc_other instanceof ImcCONST other_const) {
                                var v = other_const.value;
                                if (v == 2 || v == 4 || v == 8 || v == 16) {
                                    imc_other = imc_const;
                                    imc_const = other_const;
                                }
                            }

                            var addu_val = imc_const.value;

                            if (addu_val!=2 && addu_val!=4 && addu_val!=8 && addu_val!=16)
                                return Tile.Invalid();


                            var const_result = match_any_tile(imc_const);
                            var other_result = match_any_tile(imc_other);

                            if (!const_result.is_valid() || !other_result.is_valid()) return Tile.Invalid();

                            var instructions = new Vector<AsmInstr>();
                            instructions.addAll(const_result.instructions);
                            instructions.addAll(other_result.instructions);

                            long cost = const_result.cost + other_result.cost;

                            var dst = new MemTemp();

                            cost += 1;
                            instructions.add(new AsmOPER(
                                    String.format("\t\t%dADDU\t`d0,`s0,`s1", addu_val),
                                    new Vector<>(List.of(const_result.temp, other_result.temp)),
                                    new Vector<>(List.of(dst)),
                                    new Vector<>()
                            ));

                            return new MatchingResult(cost, dst, instructions);
                        }
                    },
                    // Binary operations
                    new Tile() {
                        @Override
                        public MatchingResult try_match(ImcExpr expr) {
                            if (!(expr instanceof ImcBINOP binop)) return Tile.Invalid();
                            var lhs_result = match_any_tile(binop.fstExpr);
                            var rhs_result = match_any_tile(binop.sndExpr);
                            if (!lhs_result.is_valid() || !rhs_result.is_valid())
                                return Tile.Invalid();

                            var instructions = new Vector<AsmInstr>();

                            instructions.addAll(lhs_result.instructions);
                            instructions.addAll(rhs_result.instructions);

                            var dst = new MemTemp();

                            long cost = lhs_result.cost + rhs_result.cost;
                            switch (binop.oper) {
                                case EQU, NEQ, LTH, GTH, LEQ, GEQ -> {
                                    cost += 2;
                                    instructions.add(new AsmOPER(
                                            "\t\tCMP\t`d0,`s0,`s1",
                                            new Vector<>(List.of(lhs_result.temp, rhs_result.temp)),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                    instructions.add(new AsmOPER(
                                            String.format("\t\tZS%s\t`d0,`s0,1", switch (binop.oper) {
                                                case EQU -> "Z";
                                                case NEQ -> "NZ";
                                                case LTH -> "N";
                                                case GTH -> "P";
                                                case LEQ -> "NP";
                                                case GEQ -> "NN";
                                                default ->
                                                        throw new IllegalStateException("Unexpected value: " + binop.oper);
                                            }),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                }
                                case OR, AND, ADD, SUB -> {
                                    cost += 1;
                                    instructions.add(new AsmOPER(
                                            String.format("\t\t%s\t`d0,`s0,`s1", binop.oper),
                                            new Vector<>(List.of(lhs_result.temp, rhs_result.temp)),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                }
                                case MUL -> {
                                    cost += 10;
                                    instructions.add(new AsmOPER(
                                            "\t\tMUL\t`d0,`s0,`s1",
                                            new Vector<>(List.of(lhs_result.temp, rhs_result.temp)),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                }
                                case DIV -> {
                                    cost += 60;
                                    instructions.add(new AsmOPER(
                                            "\t\tDIV\t`d0,`s0,`s1",
                                            new Vector<>(List.of(lhs_result.temp, rhs_result.temp)),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                }
                                case MOD -> {
                                    cost += 61;
                                    instructions.add(new AsmOPER(
                                            "\t\tDIV\t`d0,`s0,`s1",
                                            new Vector<>(List.of(lhs_result.temp, rhs_result.temp)),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                    instructions.add(new AsmOPER(
                                            "\t\tGET\t`d0,rR",
                                            new Vector<>(),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                }
                            }

                            return new MatchingResult(cost, dst, instructions);
                        }
                    },
                    // Call
                    new Tile() {
                        @Override
                        public MatchingResult try_match(ImcExpr expr) {
                            if (!(expr instanceof ImcCALL call)) return Tile.Invalid();

                            long cost = 0;
                            var instructions = new Vector<AsmInstr>();

                            int i = 0;
                            for (var arg : call.args) {
                                var arg_result = match_any_tile(arg);
                                if (!arg_result.is_valid()) return Tile.Invalid();

                                cost += arg_result.cost;
                                instructions.addAll(arg_result.instructions);

                                cost += 10;

                                var offset = call.offs.get(i++);
                                if (offset < 256) {
                                    instructions.add(new AsmOPER(
                                            String.format("\t\tSTO\t`s0,$254,%d", offset),
                                            new Vector<>(List.of(arg_result.temp)),
                                            new Vector<>(),
                                            new Vector<>()
                                    ));
                                } else {
                                    var const_result = match_any_tile(new ImcCONST(offset));
                                    instructions.addAll(const_result.instructions);
                                    instructions.add(new AsmOPER(
                                            "\t\tSTO\t`s0,$254,`s1",
                                            new Vector<>(List.of(arg_result.temp, const_result.temp)),
                                            new Vector<>(),
                                            new Vector<>()
                                    ));
                                }
                            }

                            cost += 1;
                            instructions.add(new AsmOPER(
                                    String.format("\t\tPUSHJ\t$252,%s", call.label.name),
                                    new Vector<>(),
                                    new Vector<>(),
                                    new Vector<>(List.of(call.label))
                            ));

                            var dst = new MemTemp();

                            cost += 10;
                            instructions.add(new AsmOPER(
                                    "\t\tLDO\t`d0,$254,0",
                                    new Vector<>(),
                                    new Vector<>(List.of(dst)),
                                    new Vector<>()
                            ));

                            return new MatchingResult(cost, dst, instructions);
                        }
                    },
                    // Constants
                    new Tile() {
                        @Override
                        public MatchingResult try_match(ImcExpr expr) {
                            if (!(expr instanceof ImcCONST cons)) return Tile.Invalid();

                            var value = cons.value;

                            var instructions = new Vector<AsmInstr>();
                            long cost = 0;

                            var dst = new MemTemp();

                            cost += 1;
                            instructions.add(new AsmOPER(
                                    String.format("\t\tSET\t`d0,%d", value & 0xffff),
                                    new Vector<>(),
                                    new Vector<>(List.of(dst)),
                                    new Vector<>()
                            ));

                            if ((value & 0xffff0000L) != 0) {
                                cost += 1;
                                instructions.add(new AsmOPER(
                                        String.format("\t\tORML\t`d0,%d", (value & 0xffff0000L) >>> 16),
                                        new Vector<>(),
                                        new Vector<>(List.of(dst)),
                                        new Vector<>()
                                ));
                            }

                            if ((value & 0xffff00000000L) != 0) {
                                cost += 1;
                                instructions.add(new AsmOPER(
                                        String.format("\t\tORMH\t`d0,%d", (value & 0xffff00000000L) >>> 32),
                                        new Vector<>(),
                                        new Vector<>(List.of(dst)),
                                        new Vector<>()
                                ));
                            }

                            if ((value & 0xffff000000000000L) != 0) {
                                cost += 1;
                                instructions.add(new AsmOPER(
                                        String.format("\t\tORH\t`d0,%d", (value & 0xffff000000000000L) >>> 48),
                                        new Vector<>(),
                                        new Vector<>(List.of(dst)),
                                        new Vector<>()
                                ));
                            }

                            return new MatchingResult(cost, dst, instructions);
                        }
                    },
                    // Unary operations
                    new Tile() {
                        @Override
                        public MatchingResult try_match(ImcExpr expr) {
                            if (!(expr instanceof ImcUNOP unop)) return Tile.Invalid();

                            var sub_result = match_any_tile(unop.subExpr);

                            if (!sub_result.is_valid()) return Tile.Invalid();

                            var instructions = new Vector<AsmInstr>(sub_result.instructions);
                            long cost = sub_result.cost;
                            var dst = new MemTemp();

                            switch (unop.oper) {
                                case NOT -> {
                                    cost += 1;
                                    instructions.add(new AsmOPER(
                                            "\t\tZSZ\t`d0,`s0,1",
                                            new Vector<>(List.of(dst)),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                }
                                case NEG -> {
                                    cost += 1;
                                    instructions.add(new AsmOPER(
                                            "\t\tNEG\t`d0,`s0",
                                            new Vector<>(List.of(sub_result.temp)),
                                            new Vector<>(List.of(dst)),
                                            new Vector<>()
                                    ));
                                }
                            }

                            return new MatchingResult(cost, dst, instructions);
                        }
                    }
            ));


    private final HashMap<ImcExpr, Tile.MatchingResult> matching_lookup = new HashMap<>();
    private Tile.MatchingResult match_any_tile(ImcExpr expr) {
        if (matching_lookup.containsKey(expr)) {
            return matching_lookup.get(expr);
        }

        Tile.MatchingResult best_result = Tile.Invalid();
        for (var tile: tiles) {
            var result = tile.try_match(expr);

            if (result.cost < best_result.cost) {
                best_result = result;
            }
        }

        matching_lookup.put(expr, best_result);

        return best_result;
    }

    private MemTemp visit_expr(ImcExpr expr, Vector<AsmInstr> instructions) {
        var best_match = match_any_tile(expr);
        if (!best_match.is_valid()) {
            System.err.printf("Unmatched tile for '%s'%n", expr);
            //throw new Report.InternalError();
            return new MemTemp();
        }
        instructions.addAll(best_match.instructions);
        return best_match.temp;
    }

    @Override
    public MemTemp visit(ImcBINOP binOp, Vector<AsmInstr> instructions) {
        return visit_expr(binOp, instructions);
    }

    @Override
    public MemTemp visit(ImcCALL call, Vector<AsmInstr> instructions) {
        return visit_expr(call, instructions);
    }

    @Override
    public MemTemp visit(ImcCONST constant, Vector<AsmInstr> instructions) {
        return visit_expr(constant, instructions);
    }

    @Override
    public MemTemp visit(ImcMEM mem, Vector<AsmInstr> instructions) {
        return visit_expr(mem, instructions);
    }

    @Override
    public MemTemp visit(ImcNAME name, Vector<AsmInstr> instructions) {
        return visit_expr(name, instructions);
    }

    @Override
    public MemTemp visit(ImcTEMP temp, Vector<AsmInstr> instructions) {
        return visit_expr(temp, instructions);
    }

    @Override
    public MemTemp visit(ImcUNOP unOp, Vector<AsmInstr> instructions) {
        return new MemTemp();
    }
}
