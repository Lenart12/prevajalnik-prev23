package prev23.phase.memory;

import prev23.common.report.Report;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.visitor.*;
import prev23.data.typ.*;
import prev23.data.mem.*;
import prev23.phase.seman.*;

import java.util.HashMap;

/**
 * Computing memory layout: frames and accesses.
 */
public class MemEvaluator extends AstFullVisitor<Object, MemEvaluator.MemScope> {


    public static class MemScope {
        public int depth = 0;
        public long locals_size = 0;
        public long args_size = 0;
        public long params_size = 8;
        public HashMap<String, AstNameDecl> exported_names = new HashMap<>();

        private MemScope() {}

        public MemScope sub_scope() {
            var sub_scope = new MemScope();
            sub_scope.depth = depth + 1;
            sub_scope.exported_names = exported_names;
            return sub_scope;
        }

        public MemScope record_scope() {
            var record_scope = new MemScope();
            record_scope.depth = -1;

            return record_scope;
        }

        private MemLabel get_label(AstNameDecl decl) {
            if (depth <= 1 && !exported_names.containsKey(decl.name)) {
                exported_names.put(decl.name, decl);
                return new MemLabel(decl.name);
            } else {
                return new MemLabel();
            }
        }

        public void add_frame(AstFunDecl decl) {
            var frame = new MemFrame(get_label(decl), depth, -locals_size, args_size);
            Memory.frames.put(decl, frame);
        }

        public void reserve_args(AstCallExpr call) {
            var call_args_size = 8;
            var fun_decl = (AstFunDecl)SemAn.declaredAt.get(call);

            for (var par: fun_decl.pars) {
                call_args_size += SemAn.isType.get(par.type).size();
            }

            args_size = Math.max(args_size, call_args_size);
        }
        
        public void add_access(AstMemDecl decl) {
            MemAccess access = null;

            var type = SemAn.isType.get(decl.type);
            if (type == null) throw new Report.InternalError();
            var mem_size = type.size();

            if (depth == 0) {
                access = new MemAbsAccess(mem_size, get_label(decl));
            } else {
                var access_depth = Math.max(0, depth);

                if (decl instanceof AstParDecl) {
                    access = new MemRelAccess(mem_size, params_size, access_depth);
                    params_size += mem_size;
                } else if (decl instanceof AstVarDecl) {
                    locals_size -= mem_size;
                    access = new MemRelAccess(mem_size, locals_size, access_depth);
                } else if (decl instanceof AstCmpDecl) {
                    access = new MemRelAccess(mem_size, locals_size, access_depth);
                    locals_size += mem_size;
                }
            }

            Memory.accesses.put(decl, access);
        }
    }

    // DECLARATIONS

    @Override
    public Object visit(AstTrees<?> trees, MemScope scope) {
        if (scope == null) scope = new MemScope();
        return super.visit(trees, scope);
    }

    @Override
    public Object visit(AstVarDecl varDecl, MemScope scope) {
        scope.add_access(varDecl);
        varDecl.type.accept(this, scope);
        return null;
    }

    @Override
    public Object visit(AstRecType recType, MemScope scope) {
        var record_scope = scope.record_scope();
        for (var comp: recType.comps) {
            comp.accept(this, record_scope);
        }
        return null;
    }
    @Override
    public Object visit(AstCmpDecl cmpDecl, MemScope scope) {
        scope.add_access(cmpDecl);
        cmpDecl.type.accept(this, scope);
        return null;
    }

    @Override
    public Object visit(AstFunDecl funDecl, MemScope scope) {
        var function_scope = scope.sub_scope();

        for (var pars: funDecl.pars) {
            pars.accept(this, function_scope);
        }

        if (funDecl.stmt != null) {
            funDecl.stmt.accept(this, function_scope);
        }

        function_scope.add_frame(funDecl);
        return null;
    }

    @Override
    public Object visit(AstParDecl parDecl, MemScope scope) {
        scope.add_access(parDecl);
        return null;
    }

    // EXPRESSIONS

    @Override
    public Object visit(AstCallExpr callExpr, MemScope scope) {
        scope.reserve_args(callExpr);
        super.visit(callExpr, scope);
        return null;
    }

    @Override
    public Object visit(AstAtomExpr atomExpr, MemScope scope) {
        if (atomExpr.type != AstAtomExpr.Type.STR)
            return null;

        var value = atomExpr.value.substring(1, atomExpr.value.length() - 1).replace("\\\"", "\"");
        var const_size = (value.length() + 1) * (new SemChar().size());

        Memory.strings.put(atomExpr, new MemAbsAccess(const_size, new MemLabel(), value));
        return null;
    }

}
