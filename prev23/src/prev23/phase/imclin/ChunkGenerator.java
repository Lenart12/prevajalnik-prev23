package prev23.phase.imclin;

import java.util.*;

import prev23.common.report.Report;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.visitor.*;
import prev23.data.mem.*;
import prev23.data.imc.code.stmt.*;
import prev23.data.lin.*;
import prev23.phase.imcgen.*;
import prev23.phase.memory.*;

public class ChunkGenerator extends AstFullVisitor<Object, Object> {
    @Override
    public Object visit(AstFunDecl funDecl, Object o) {
        if (funDecl.stmt == null) return null;

        var frame = Memory.frames.get(funDecl);
        var body = ImcGen.stmtImc.get(funDecl.stmt).accept(new StmtCanonizer(), null);

        var entry_label = new MemLabel();
        var exit_label = new MemLabel();

        body.add(0, new ImcLABEL(entry_label));
        body.add(new ImcJUMP(exit_label));

        var fixed = fix_basic_blocks(body, entry_label, exit_label);

        ImcLin.addCodeChunk(new LinCodeChunk(frame, fixed, entry_label, exit_label));

        funDecl.stmt.accept(this, o);

        return null;
    }

    @Override
    public Object visit(AstVarDecl varDecl, Object o) {
        var access = Memory.accesses.get(varDecl);
        if (access instanceof MemAbsAccess abs) {
            ImcLin.addDataChunk(
                new LinDataChunk(abs)
            );
        }
        return null;
    }

    @Override
    public Object visit(AstAtomExpr atomExpr, Object o) {
       if (atomExpr.type == AstAtomExpr.Type.STR) {
           ImcLin.addDataChunk(
               new LinDataChunk(Memory.strings.get(atomExpr))
           );
       }
       return null;
    }

    public static final HashSet<String> nonremovable_negative_labels = new HashSet<>();

    private Vector<ImcStmt> fix_basic_blocks(Vector<ImcStmt> stmts, MemLabel entry_label, MemLabel exit_label) {
        HashMap<MemLabel, Vector<ImcStmt>> basic_blocks = new HashMap<>();

        Vector<ImcStmt> block = null;

        for (var stmt : stmts) {
            if (stmt instanceof ImcLABEL label) {
                if (block != null) {
                    throw new Report.InternalError();
                }
                block = new Vector<>();
                basic_blocks.put(label.label, block);
            }

            // Skip dead code
            if (block == null) continue;

            block.add(stmt);

            if (stmt instanceof ImcJUMP || stmt instanceof ImcCJUMP) {
                block = null;
            }
        }

        if (block != null) {
            throw new Report.InternalError();
        }

        var fixed = new Vector<ImcStmt>();

        // Insert basic blocks starting with entry_label
        var next_blocks = new LinkedList<MemLabel>();
        next_blocks.addFirst(entry_label);
        while(!next_blocks.isEmpty()) {
            var next_block_label = next_blocks.removeFirst();

            // Skip exit block
            if (next_block_label.name.equals(exit_label.name)) continue;

            var next_block = basic_blocks.get(next_block_label);

            if (next_block == null) {
                continue;
            }

            fixed.addAll(next_block);
            basic_blocks.remove(next_block_label);

            var next_jump = next_block.lastElement();

            if (next_jump instanceof ImcJUMP jump) {
                next_blocks.addFirst(jump.label);
            } else if (next_jump instanceof ImcCJUMP cjump) {
                next_blocks.addFirst(cjump.posLabel);
                next_blocks.addFirst(cjump.negLabel);
            }
        }

        boolean continue_reducing = true;
        while (continue_reducing) {
            continue_reducing = false;

            // Remove duplicate labels
            var duplicate_mapping = new HashMap<MemLabel, MemLabel>();
            MemLabel current_set = null;
            for (var instr : fixed) {
                if (!(instr instanceof ImcLABEL label)) {
                    current_set = null;
                    continue;
                }

                if (current_set == null) {
                    current_set = label.label;
                } else {
                    duplicate_mapping.put(label.label, current_set);
                }
            }

            if (!duplicate_mapping.isEmpty()) {
                var it = fixed.iterator();
                while (it.hasNext()) {
                    var instr = it.next();
                    if (instr instanceof ImcLABEL label && duplicate_mapping.containsKey(label.label)) {
                        it.remove();
                        continue_reducing = true;
                    } else if (instr instanceof ImcCJUMP cjump) {
                        cjump.posLabel = duplicate_mapping.getOrDefault(cjump.posLabel, cjump.posLabel);
                        cjump.negLabel = duplicate_mapping.getOrDefault(cjump.negLabel, cjump.negLabel);
                    } else if (instr instanceof ImcJUMP jump) {
                        jump.label = duplicate_mapping.getOrDefault(jump.label, jump.label);
                    }
                }
            }

            // Remove sequential JUMP L -> LABEL L
            var targeted_labels = new HashSet<MemLabel>();
            targeted_labels.add(entry_label);
            for (int i = 0; i < fixed.size(); i++) {
                if (fixed.get(i) instanceof ImcCJUMP cjump) {
                    nonremovable_negative_labels.add(cjump.posLabel.name);
                    targeted_labels.add(cjump.posLabel);
                    targeted_labels.add(cjump.negLabel);
                } else if (fixed.get(i) instanceof ImcJUMP jump) {
                    if (i + 1 < fixed.size() && fixed.get(i + 1) instanceof ImcLABEL label && jump.label == label.label) {
                        fixed.remove(i--);
                        continue_reducing = true;
                    } else {
                        targeted_labels.add(jump.label);
                        nonremovable_negative_labels.add(jump.label.name);
                    }
                }
            }

            // Remove unused labels
            for (int i = 0; i < fixed.size(); i++) {
                if (fixed.get(i) instanceof ImcLABEL label) {
                    if (!targeted_labels.contains(label.label)) {
                        continue_reducing = true;
                        fixed.remove(i--);
                    }
                }
            }
        }

        return fixed;
    }
}
