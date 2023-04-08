package prev23.phase.imclin;

import java.util.*;

import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.visitor.*;
import prev23.data.mem.*;
import prev23.data.imc.code.expr.*;
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

        ImcLin.addCodeChunk(new LinCodeChunk(frame, body, entry_label, exit_label));
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
}
