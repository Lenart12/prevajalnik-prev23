package prev23.phase.seman;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

import prev23.common.report.*;
import prev23.data.ast.tree.*;
import prev23.data.ast.tree.decl.*;
import prev23.data.ast.tree.expr.*;
import prev23.data.ast.tree.type.*;
import prev23.data.ast.tree.stmt.*;
import prev23.data.ast.visitor.*;
import prev23.data.typ.*;


public class TypeResolver extends AstFullVisitor<SemType, Object> {

    public final HashMap<SemRec, HashMap<String, SemType>> record_component_names = new HashMap<>();
    public final HashMap<SemRec, HashMap<String, AstCmpDecl>> record_component_decls = new HashMap<>();

    private static boolean is_void(SemType type) {
        return type.actualType() instanceof SemVoid;
    }

    private static boolean is_comparable(SemType type) {
        var t = type.actualType().getClass();
        return t.equals(SemChar.class) ||
               t.equals(SemInt.class)  ||
               t.equals(SemPtr.class);
    }

    private static boolean is_primitive(SemType type) {
        var t = type.actualType().getClass();
        return t.equals(SemVoid.class) ||
               t.equals(SemChar.class) ||
               t.equals(SemInt.class)  ||
               t.equals(SemBool.class) ||
               t.equals(SemPtr.class);
    }

    private static boolean is_same_type(SemType t1, SemType t2) {
        return is_same_type(t1, t2, new HashSet<>());
    }


    private static boolean is_same_type(SemType t1, SemType t2, Set<Set<SemType>> equal_types) {
//        System.out.printf("Comparing %s and %s%n", type_to_string(t1), type_to_string(t2));
        if (t1 == t2) return true;

        if (t1 instanceof SemName n1 && t2 instanceof SemName n2) {
            if (equal_types.contains(Set.of(n1, n2))) return true;

            equal_types.add(Set.of(n1, n2));
        }

        t1 = t1.actualType();
        t2 = t2.actualType();

        if (t1 instanceof SemArr a1 && t2 instanceof  SemArr a2) {
            return a1.numElems == a2.numElems && is_same_type(a1.elemType, a2.elemType, equal_types);
        }

        else if (t1 instanceof SemPtr p1 && t2 instanceof SemPtr p2) {
            return is_same_type(p1.baseType, p2.baseType, equal_types);
        }

        else if (t1 instanceof SemRec r1 && t2 instanceof SemRec r2) {
            if (r1.numCmps() != r2.numCmps()) return false;

            for (var i = 0; i < r1.numCmps(); i++) {
                if (!is_same_type(r1.cmpType(i), r2.cmpType(i), equal_types)) return false;
            }
        }

        return t1.getClass().equals(t2.getClass());
    }

    private static boolean is_non_void_primitive(SemType type) {
        return !is_void(type) && is_primitive(type);
    }

    private String type_to_string(SemType type) {
        return type_to_string(type, new HashSet<>());
    }

    private String type_to_string(SemType type, HashSet<String> visited_names) {
        if (type == null) return "!null_type!";
        if (type instanceof SemArr arr) return String.format("arr(%d x %s)", arr.numElems, type_to_string(arr.elemType, visited_names));
        else if (type instanceof SemBool) return "bool";
        else if (type instanceof SemChar) return "char";
        else if (type instanceof SemInt) return "int";
        else if (type instanceof SemName name) {
            if (visited_names.contains(name.name)) {
                return String.format("%s=`...`", name.name);
            }
            visited_names.add(name.name);
            return String.format("%s=`%s`", name.name, type_to_string(name.type(), visited_names));
        }
        else if (type instanceof SemPtr ptr) return String.format("ptr(%s)", type_to_string(ptr.baseType, visited_names));
        else if (type instanceof SemRec rec)
            return String.format("{%s}", record_component_names.get(rec).entrySet().stream().map(
                    el -> String.format("%s: %s", el.getKey(), type_to_string(el.getValue(), visited_names))
            ).collect(Collectors.joining(", ")));
        else if (type instanceof SemVoid) return "void";
        System.out.println(type);
        throw new Report.InternalError();
    }

    private void expect_comparable(SemType type, Locatable ast, String message) {
        if (is_comparable(type)) return;
        throw new Report.Error(ast, String.format((!message.isEmpty()) ? message :
                "Expected comparable type but got '%s'", type_to_string(type)));
    }

    private void expect_primitive(SemType type, Locatable ast, String message) {
        if (is_primitive(type)) return;
        throw new Report.Error(ast, String.format((!message.isEmpty()) ? message :
                "Expected primitive type, but got '%s'", type_to_string(type)));
    }

    private void expect_non_void_primitive(SemType type, Locatable ast, String message) {
        if (is_non_void_primitive(type)) return;
        throw new Report.Error(ast, String.format((!message.isEmpty()) ? message :
                "Expected non void primitive type, but got '%s'", type_to_string(type)));
    }

    private void expect_same_type(SemType t1, SemType t2, Locatable ast, String message) {
        if (is_same_type(t1, t2)) return;
        throw new Report.Error(ast, String.format((!message.isEmpty()) ? message :
                "Expected two of same type, but got '%s' and '%s'", type_to_string(t1), type_to_string(t2)));
    }


    private void expect_data_type(SemType type, Locatable ast, String message) {
        var aClass = type.actualType().getClass();
        if (aClass.equals(SemVoid.class) ||
            aClass.equals(SemChar.class) ||
            aClass.equals(SemInt.class) ||
            aClass.equals(SemBool.class) ||
            aClass.equals(SemArr.class) ||
            aClass.equals(SemRec.class) ||
            aClass.equals(SemPtr.class)) {
            return;
        }
        throw new Report.Error(ast, String.format((!message.isEmpty()) ? message :
                "Expected data type, but got '%s'", type_to_string(type)));
    }

    private void expect_non_void_data_type(SemType type, Locatable ast, String message) {
        if (type.actualType() instanceof SemVoid) {
            throw new Report.Error(ast, String.format((!message.isEmpty()) ? message :
                    "Expected non void data type, but got '%s'", type_to_string(type)));
        }
        expect_data_type(type, ast, message);
    }
    private void expect_castable(SemType type_from, SemType type_to, Locatable ast, String message) {
        if (is_non_void_primitive(type_from) && is_non_void_primitive(type_to)) return;
        throw new Report.Error(ast, String.format((!message.isEmpty()) ? message :
                "Type '%s' cannot be cast to type '%s'", type_to_string(type_from), type_to_string(type_to)));
    }

    private <T> T expect(Object o, Class<T> expected_class, String message) {
        return expect(o, expected_class, null, message);
    }

    private <T> T expect(Object o, Class<T> expected_class, Locatable ast, String message) {
        if (expected_class.isInstance(o)) return expected_class.cast(o);

        var error = String.format((!message.isEmpty()) ? message: "Unexpected type '%s', expected '%s'",
                (o instanceof SemType sem)
                        ? type_to_string(sem)
                        : o.getClass(),
                expected_class
        );
        if (ast != null) throw new Report.Error(ast, error);
        if (o instanceof Locatable l) throw new Report.Error(l, error);
        throw new Report.Error(error);
    }

    private <T extends SemType> T accept_and_expect_actual(AstTree ast, Class<T> type, String message){
        return expect(ast.accept(this, null).actualType(), type, ast, message);
    }

    private <T extends SemType> T accept_and_expect(AstTree ast, Class<T> type, String message) {
        return expect(ast.accept(this, null), type, ast, message);
    }

    private static SemType declare_is(AstType ast, SemType t) {
        SemAn.isType.put(ast, t);
        return t;
    }

    private static SemType declare_of(AstExec ast, SemType t) {
        SemAn.ofType.put(ast, t);
        return t;
    }

    private void TypeError(Locatable locatable, String message) {
        throw new Report.Error(locatable.location(), message);
    }
    private void UnexpectedNull() {
        throw new Report.InternalError();
    }


    // GENERAL PURPOSE

    private SemType visit_block_statements(AstTrees<? extends  AstTree> trees, Object arg) {
        SemType last = new SemVoid();
        for (AstTree t : trees)
            if (t == null) UnexpectedNull();
            else last = t.accept(this, arg);

        return last;
    }

    private SemType visit_record_declarations(AstTrees<AstCmpDecl> trees, Object arg) {
        var cmp_types = new Vector<SemType>(trees.size());
        var cmp_names = new LinkedHashMap<String, SemType>();
        var cmp_decls = new LinkedHashMap<String, AstCmpDecl>();
        for (AstCmpDecl t : trees) {
            if (t == null) UnexpectedNull();
            if (cmp_names.containsKey(t.name)) {
                TypeError(t, String.format("Duplicate record component name '%s'", t.name));
            }

            var cmp_type = t.accept(this, arg);
            expect_non_void_data_type(cmp_type, t, "Records can only have non void data types, but got '%s'");
            cmp_types.add(cmp_type);
            cmp_names.put(t.name, cmp_type);
            cmp_decls.put(t.name, t);
        }

        var rec = new SemRec(cmp_types);
        record_component_names.put(rec, cmp_names);
        record_component_decls.put(rec, cmp_decls);
        return rec;
    }

    private static class NameTypeDependenciesCollector extends AstFullVisitor<Object, Object> {
        public HashSet<String> included_names = new HashSet<>();

        public static class NameIsNotATypeDeclarationException extends RuntimeException {
            public AstNameType offending_type;
            public NameIsNotATypeDeclarationException(AstNameType name_type) {
                this.offending_type = name_type;
            }
        }

        @Override
        public SemType visit(AstNameType nameType, Object arg) {
            var declared_at = SemAn.declaredAt.get(nameType);

            if (!(declared_at instanceof AstTypDecl)) {
                throw new NameIsNotATypeDeclarationException(nameType);
            }

            var declared_type = SemAn.declaresType.get((AstTypDecl) declared_at);

            if (declared_type == null)
                included_names.add(nameType.name);
            return null;
        }

        @Override
        public SemType visit(AstPtrType ptrType, Object arg) {
            return null;
        }
    }

    private static void mark_resolved(HashMap<AstTypDecl, HashSet<String>> decls, AstTypDecl resolved_decl) {
        decls.remove(resolved_decl);

        for (var unchecked: decls.entrySet()) {
            var dependencies = unchecked.getValue();
            dependencies.remove(resolved_decl.name);
        }
    }

    private SemType visit_type_declarations(AstTrees<AstTypDecl> trees, Object arg) {
        var unchecked_types = new HashMap<AstTypDecl, HashSet<String>>();

        for (var decl : trees) {
            if (decl == null) UnexpectedNull();

            var dependencies_visitor = new NameTypeDependenciesCollector();
            try {
                decl.accept(dependencies_visitor, null);
            } catch (NameTypeDependenciesCollector.NameIsNotATypeDeclarationException e) {
                TypeError(e.offending_type, String.format(
                        "'%s' does not define a type and can not be used as one", e.offending_type.name));
            }
            unchecked_types.put(decl, dependencies_visitor.included_names);
        }

        for (var decl: trees) {
            SemAn.declaresType.put(decl, new SemName(decl.name));
        }

        while (!unchecked_types.isEmpty()) {
            boolean some_resolved = false;
            for (var unchecked : Set.copyOf(unchecked_types.entrySet())) {
                var decl = unchecked.getKey();
                var dependencies = unchecked.getValue();

//                System.out.printf("Trying to resolve %s %s%n", decl.name, dependencies);
                if (!dependencies.isEmpty()) continue;


                decl.accept(this, arg);
                mark_resolved(unchecked_types, decl);
                some_resolved = true;

//                System.out.printf("Resolved %s%n", decl.name);
            }

            if (!some_resolved) {
                var unresolved_names = unchecked_types.keySet().stream().map(
                        astTypDecl -> astTypDecl.name
                ).collect(Collectors.joining(", "));
                TypeError(trees, String.format("Set of '%s' contains a cycle and cannot be resolved", unresolved_names));
            }
        }

        return new SemVoid();
    }

    private SemType visit_function_declarations(AstTrees<AstFunDecl> trees, Object arg) {
        for (var decl: trees) {
            if (decl.pars == null || decl.type == null) UnexpectedNull();

            accept_and_expect(decl.pars, SemVoid.class, "");

            var fun_type = decl.type.accept(this, arg);
            expect_primitive(fun_type, decl.type, "Function return type must be primitive, but got '%s'");

            SemAn.isType.put(decl.type, fun_type);
        }

        for (var decl: trees) {
            if (decl.stmt == null) continue;

            var stmt_type = decl.stmt.accept(this, arg);

            var fun_type = SemAn.isType.get(decl.type);
            if (fun_type == null) UnexpectedNull();

            expect_same_type(fun_type, stmt_type, decl,
                    "Function declared return '%s' does not match statement return '%s'");
        }

        return new SemVoid();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SemType visit(AstTrees<? extends AstTree> trees, Object arg) {
        switch (trees.title) {
            case "call parameters":
            case "declarations":
            case "function arguments":
            case "variable declarations":
                break;
            case "function declarations":
                return visit_function_declarations((AstTrees<AstFunDecl>) trees, arg);
            case "block statements":
                return visit_block_statements(trees, arg);
            case "record declarations":
                return visit_record_declarations((AstTrees<AstCmpDecl>) trees, arg);
            case "type declarations":
                return visit_type_declarations((AstTrees<AstTypDecl>) trees, arg);
            default:
                throw new Report.InternalError();
        }

        for (AstTree t : trees)
            if (t == null) UnexpectedNull();
            else t.accept(this, arg);

        return new SemVoid();
    }

    // DECLARATIONS

    @Override
    public SemType visit(AstCmpDecl cmpDecl, Object arg) {
        if (cmpDecl.type == null) UnexpectedNull();

        return cmpDecl.type.accept(this, arg);
    }

    @Override
    public SemType visit(AstFunDecl funDecl, Object arg) {
        // must be in visit_function_declarations()
        throw new Report.InternalError();
    }

    @Override
    public SemType visit(AstParDecl parDecl, Object arg) {
        if (parDecl.type == null) UnexpectedNull();
        
        var par_type = parDecl.type.accept(this, arg);
        expect_non_void_primitive(par_type, parDecl.type, "Parameters can only have non void primitive types, but got '%s'");
        return par_type;
    }

    @Override
    public SemType visit(AstTypDecl typDecl, Object arg) {
        if (typDecl.type == null) UnexpectedNull();
        
        var decl_type = typDecl.type.accept(this, arg);
        SemAn.declaresType.get(typDecl).define(decl_type);

        return decl_type;
    }

    @Override
    public SemType visit(AstVarDecl varDecl, Object arg) {
        if (varDecl.type == null) UnexpectedNull();
        var decl_type = varDecl.type.accept(this, arg);
        expect_non_void_data_type(decl_type, varDecl.type, "Variables can only have non void data types, but got '%s'");

        return decl_type;
    }

    // EXPRESSIONS

    @Override
    public SemType visit(AstArrExpr arrExpr, Object arg) {
        if (arrExpr.arr == null || arrExpr.idx == null) UnexpectedNull();
        var arr_type = accept_and_expect_actual(arrExpr.arr, SemArr.class, "Type '%s' cannot be indexed as an array");
        accept_and_expect_actual(arrExpr.idx, SemInt.class, "Type '%s' cannot be used to index an array");
        return declare_of(arrExpr, arr_type.elemType);
    }

    @Override
    public SemType visit(AstAtomExpr atomExpr, Object arg) {
        var atom_type = switch (atomExpr.type) {
            case VOID -> new SemVoid();
            case CHAR -> new SemChar();
            case INT -> new SemInt();
            case BOOL -> new SemBool();
            case PTR -> new SemPtr(new SemVoid());
            case STR -> new SemPtr(new SemChar());
        };

        return declare_of(atomExpr, atom_type);
    }

    @Override
    public SemType visit(AstBinExpr binExpr, Object arg) {
        if (binExpr.fstExpr == null || binExpr.sndExpr == null) UnexpectedNull();

        return switch (binExpr.oper) {
            case OR, AND -> {
                accept_and_expect_actual(binExpr.fstExpr, SemBool.class,
                        "Lhs-operand of binary expression must be bool, but got '%s'");
                accept_and_expect_actual(binExpr.sndExpr, SemBool.class,
                        "Rhs-operand of binary expression must be bool, but got '%s'");
                yield declare_of(binExpr, new SemBool());
            }
            case EQU, NEQ -> {
                var lhs = binExpr.fstExpr.accept(this, arg);
                var rhs = binExpr.sndExpr.accept(this, arg);
                expect_non_void_primitive(lhs, binExpr.fstExpr,
                        "Binary expression operands can only have non void primitives, but got '%s'");
                expect_same_type(lhs, rhs, binExpr,
                        "Operands of binary expression must be of equal types but got lhs:'%s', rhs:'%s'");
                yield declare_of(binExpr, new SemBool());
            }
            case LTH, GTH, LEQ, GEQ -> {
                var lhs = binExpr.fstExpr.accept(this, arg);
                var rhs = binExpr.sndExpr.accept(this, arg);
                expect_comparable(lhs, binExpr.fstExpr,
                        "Type '%s' is not comparable");
                expect_same_type(lhs, rhs, binExpr,
                        "Operands of binary expression must be of equal types but got lhs:'%s', rhs:'%s'");
                yield declare_of(binExpr, new SemBool());
            }
            case ADD, SUB, MUL, DIV, MOD -> {
                accept_and_expect_actual(binExpr.fstExpr, SemInt.class,
                        "Lhs-operand of binary expression must be int, but got '%s'");
                accept_and_expect_actual(binExpr.sndExpr, SemInt.class,
                        "Rhs-operand of binary expression must be int, but got '%s'");
                yield declare_of(binExpr, new SemInt());
            }
        };
    }


    @Override
    public SemType visit(AstCallExpr callExpr, Object arg) {
        if (callExpr.args == null) UnexpectedNull();
        var some_decl = SemAn.declaredAt.get(callExpr);
        if (some_decl == null) UnexpectedNull();
        var fun_decl = expect(some_decl, AstFunDecl.class,
                String.format("'%s' is not a callable function", callExpr.name));

        if (fun_decl.pars.size() != callExpr.args.size())
            TypeError(callExpr, String.format("Invalid call parameter size for function '%s'", callExpr.name));

        for (int i = 0; i < fun_decl.pars.size(); i++) {
            var par_type = SemAn.isType.get(fun_decl.pars.get(i).type);
            if (par_type == null) UnexpectedNull();
            var arg_type = callExpr.args.get(i).accept(this, arg);
            expect_same_type(par_type, arg_type, callExpr,
                    String.format("Function '%s' parameter '%s' expected type '%%s', but got '%%s', ",
                    fun_decl.name, fun_decl.pars.get(i).name));
        }

        return declare_of(callExpr, SemAn.isType.get(fun_decl.type));
    }

    @Override
    public SemType visit(AstCastExpr castExpr, Object arg) {
        if (castExpr.expr == null || castExpr.type == null) UnexpectedNull();

        var expr_type = castExpr.expr.accept(this, arg);
        var cast_type = castExpr.type.accept(this, arg);
        expect_castable(expr_type, cast_type, castExpr, "");
        return declare_of(castExpr, cast_type);
    }

    @Override
    public SemType visit(AstDelExpr delExpr, Object arg) {
        if (delExpr.expr == null) UnexpectedNull();
        accept_and_expect_actual(delExpr.expr, SemPtr.class,
                "Expected a pointer type to delete, but got '%s'");
        return declare_of(delExpr, new SemVoid());
    }

    @Override
    public SemType visit(AstNameExpr nameExpr, Object arg) {
        var declared_at = SemAn.declaredAt.get(nameExpr);
        if (declared_at == null) UnexpectedNull();

        if (declared_at instanceof AstTypDecl) {
            TypeError(nameExpr, String.format("'%s' is a type and can not be used as a variable or function", nameExpr.name));
        }

        else if (declared_at instanceof AstMemDecl mem_decl) {
            var mem_type = SemAn.isType.get(mem_decl.type);
            if (mem_type == null) UnexpectedNull();
            return declare_of(nameExpr, mem_type);
        }

        else if (declared_at instanceof AstFunDecl fun_decl) {
            TypeError(nameExpr, String.format("'%s' is a function and must be called", fun_decl.name));
        }

        throw new Report.InternalError();
    }

    @Override
    public SemType visit(AstNewExpr newExpr, Object arg) {
        if (newExpr.type == null) UnexpectedNull();
        var new_type = newExpr.type.accept(this, arg);
        expect_non_void_data_type(new_type, newExpr.type,
                "New can only be used with non void data types, but got '%s'");
        return declare_of(newExpr, new SemPtr(new_type));
    }

    @Override
    public SemType visit(AstPfxExpr pfxExpr, Object arg) {
        if (pfxExpr.expr == null) UnexpectedNull();
        return switch (pfxExpr.oper) {
            case ADD, SUB -> declare_of(pfxExpr, accept_and_expect_actual(pfxExpr.expr, SemInt.class,
                    "+ or - can only be prefixed with int, but got '%s'"));
            case NOT -> declare_of(pfxExpr, accept_and_expect_actual(pfxExpr.expr, SemBool.class,
                    "Negation can only be used on booleans, but got '%s'"));
            case PTR -> declare_of(pfxExpr, new SemPtr(pfxExpr.expr.accept(this, arg)));
        };
    }

    @Override
    public SemType visit(AstRecExpr recExpr, Object arg) {
        if (recExpr.rec == null || recExpr.comp == null) UnexpectedNull();
        var rec_type = accept_and_expect_actual(recExpr.rec, SemRec.class,
                "Type '%s' is not a record and can not be indexed as one");

        var comp_name = recExpr.comp.name;
        if (!record_component_names.get(rec_type).containsKey(comp_name) ||
            !record_component_decls.get(rec_type).containsKey(comp_name))
            TypeError(recExpr.comp, String.format("Record does not contain component named '%s'", comp_name));

        SemAn.declaredAt.put(recExpr.comp, record_component_decls.get(rec_type).get(comp_name));

        return declare_of(recExpr, record_component_names.get(rec_type).get(comp_name));
    }

    @Override
    public SemType visit(AstSfxExpr sfxExpr, Object arg) {
        if (sfxExpr.expr == null) UnexpectedNull();
        return switch (sfxExpr.oper) {
            case PTR -> declare_of(sfxExpr, accept_and_expect_actual(sfxExpr.expr, SemPtr.class,
                    "Expected a pointer to dereference, but got '%s'").baseType);
        };
    }

    // STATEMENTS

    @Override
    public SemType visit(AstAssignStmt assignStmt, Object arg) {
        if (assignStmt.dst == null || assignStmt.src == null) UnexpectedNull();
        var lhs_type = assignStmt.dst.accept(this, arg);
        var rhs_type = assignStmt.src.accept(this, arg);
        expect_non_void_primitive(lhs_type, assignStmt,
                "Type '%s' can not be used as a lvalue, expected non void primitive type");
        expect_same_type(lhs_type, rhs_type, assignStmt,
                "Assigned-to type '%s' is not the same type as the assigned-from '%s'");
        return declare_of(assignStmt, new SemVoid());
    }

    @Override
    public SemType visit(AstDeclStmt declStmt, Object arg) {
        if (declStmt.decls == null || declStmt.stmt == null) UnexpectedNull();
        var decls_type = declStmt.decls.accept(this, arg);
        expect(decls_type, SemVoid.class, declStmt, "");
        return declare_of(declStmt, declStmt.stmt.accept(this, arg));
    }

    @Override
    public SemType visit(AstExprStmt exprStmt, Object arg) {
        if (exprStmt.expr == null) UnexpectedNull();
        return declare_of(exprStmt, exprStmt.expr.accept(this, arg));
    }

    @Override
    public SemType visit(AstIfStmt ifStmt, Object arg) {
        if (ifStmt.cond == null || ifStmt.thenStmt == null) UnexpectedNull();

        accept_and_expect_actual(ifStmt.cond, SemBool.class,
                "Condition of if statement must be a boolean, but got '%s'");
        ifStmt.thenStmt.accept(this, arg);
        if (ifStmt.elseStmt != null) ifStmt.elseStmt.accept(this, arg);

        return declare_of(ifStmt, new SemVoid());
    }

    @Override
    public SemType visit(AstStmts stmts, Object arg) {
        if (stmts.stmts == null) UnexpectedNull();
        return declare_of(stmts, stmts.stmts.accept(this, arg));
    }

    @Override
    public SemType visit(AstWhileStmt whileStmt, Object arg) {
        if (whileStmt.cond == null || whileStmt.bodyStmt == null) UnexpectedNull();
        accept_and_expect_actual(whileStmt.cond, SemBool.class,
                "Condition of while statement must be a boolean, but got '%s'");
        whileStmt.bodyStmt.accept(this, arg);
        return declare_of(whileStmt, new SemVoid());
    }

    // TYPES

    @Override
    public SemType visit(AstArrType arrType, Object arg) {
        // T2
        if (arrType.elemType == null || arrType.numElems == null ) UnexpectedNull();

        var el_type = arrType.elemType.accept(this, arg);
        var num_type = arrType.numElems.accept(this, arg);

        expect_non_void_data_type(el_type, arrType.elemType,
                "Element type of array must be non void data type, but got '%s'");
        expect(num_type, SemInt.class, arrType.numElems,
                "Array size must be an integer constant, but got '%s'");
        var expr = expect(arrType.numElems, AstAtomExpr.class, "Expected positive non zero integer constant for array size");
        
        if (expr.type != AstAtomExpr.Type.INT) {
            TypeError(arrType.numElems, "Expected positive non zero integer constant for array size");
        }

        long num_elements = 0;
        try {
            num_elements = Long.parseLong(expr.value);
        } catch (NumberFormatException e) {
            TypeError(arrType.numElems, "Integer for array size is of wrong format");
        }

        if (num_elements <= 0) TypeError(arrType.numElems, "Array size must be larger than 0");

        return declare_is(arrType, new SemArr(el_type, num_elements));
    }

    @Override
    public SemType visit(AstAtomType atomType, Object arg) {
        // T1
        SemType type = switch(atomType.type) {
            case BOOL -> new SemBool();
            case CHAR -> new SemChar();
            case INT  -> new SemInt();
            case VOID -> new SemVoid();
        };

        return declare_is(atomType, type);
    }

    @Override
    public SemType visit(AstNameType nameType, Object arg) {
        var declared_at = SemAn.declaredAt.get(nameType);
        if (declared_at == null) UnexpectedNull();

        return declare_is(nameType, SemAn.declaresType.get(expect(declared_at, AstTypDecl.class, nameType,
                String.format("'%s' does not define a type and can not be used as one", nameType.name))));
    }

    @Override
    public SemType visit(AstPtrType ptrType, Object arg) {
        // T4
        if (ptrType.baseType == null) UnexpectedNull();

        var ptr_type = ptrType.baseType.accept(this, arg);
        // expect_data_type(ptr_type, ptrType.baseType);
        return declare_is(ptrType, new SemPtr(ptr_type));
    }

    @Override
    public SemType visit(AstRecType recType, Object arg) {
        // T3
        if (recType.comps == null) UnexpectedNull();

        return declare_is(recType, accept_and_expect_actual(recType.comps, SemRec.class,
                "Expected record"));
    }
}