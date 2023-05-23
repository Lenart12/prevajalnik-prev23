package prev23.phase.epilogue;

import prev23.Compiler;
import prev23.common.report.Report;
import prev23.data.asm.AsmInstr;
import prev23.data.asm.AsmLABEL;
import prev23.data.asm.AsmOPER;
import prev23.data.imc.code.expr.ImcCONST;
import prev23.data.imc.code.stmt.ImcCJUMP;
import prev23.data.mem.MemLabel;
import prev23.data.mem.MemTemp;
import prev23.phase.asmgen.AsmGen;
import prev23.phase.asmgen.ExprGenerator;
import prev23.phase.imclin.ChunkGenerator;
import prev23.phase.imclin.ImcLin;
import prev23.phase.regall.RegAll;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Epilogue {
    public static void finish() {
        var file_name = Compiler.cmdLineArgValue("--dst-file-name");

        try (var f = new PrintWriter(file_name)) {

            f.printf("%% $$$ Prev-23 compiler $$$ %%%n");
            f.printf("%% Generated from '%s' at '%s' %%%n%n",
                    Compiler.cmdLineArgValue("--src-file-name"),
                    new SimpleDateFormat().format(new Date()));

            f.printf("""
                    %% Global registers %%
                                
                    %% 255 Trap register
                    TrapReg\tIS\t$255
                    %% 254 Stack pointer
                    StackPtr\tGREG\t0
                    %% 253 Frame pointer
                    FramePtr\tGREG\t0
                    %% 252 Heap pointer
                    HeapPtr\tGREG\t0
                    """);

            generate_data_segment(f);
            generate_code_segment(f);
        } catch (IOException e) {
            throw new Report.Error(String.format("Cant write to '%s'%n", file_name));
        }
    }

    private static String asemblify_init_string(String init) {
        var chunks = new Vector<String>();

        for (int i = 1; i < init.length() - 1; i++) {
            var c = init.charAt(i);
            chunks.add(String.format("#%x", (int)c));
        }

        chunks.add("0");
        return String.join(",", chunks);
    }

    private static void generate_data_segment(PrintWriter f) {
        var chunks = ImcLin.dataChunks();

        f.printf("%% $$$ Data segment $$$ %%%n");
        f.printf("%% Data chunks: %d%n", chunks.size());
        f.printf("%% Total size: %dB%n", chunks.stream().mapToLong(c -> c.size).sum());

        f.printf("\t\tLOC\tData_Segment%n");
        f.printf("DataReg\tGREG\t@%n");

        for (var chunk : chunks) {
            f.printf("%% Chunk '%s', size %dB, init %s%n",
                    chunk.label.name, chunk.size, chunk.init != null ? chunk.init : "");

            f.printf("%s\t", chunk.label.name);
            if (chunk.init != null) {
                f.printf("OCTA\t%s%n", asemblify_init_string(chunk.init));
            } else {
                f.printf("%s%n", switch ((int) chunk.size) {
                    case 1 -> "BYTE";
                    case 2 -> "WYDE";
                    case 4 -> "TETRA";
                    case 8 -> "OCTA";
                    default -> String.format("BYTE%n\tLOC\t@+#%x", chunk.size - 1);
                });
            }
        }
        f.println();
    }

    private static void generate_code_segment(PrintWriter f) {
        f.printf("%% $$$ Code segment $$$ %%%n");

        f.printf("""
                        \t\tLOC\t#100
                        CodeReg\tGREG\t@
                        %% Entry point
                        Main %s %% Set stack pointer
                        \t\tSET\tFramePtr,StackPtr %% Set frame pointer
                        %s %% Set heap pointer
                        \t\tPUSHJ\t%d,_main
                        \t\tLDO\t$0,StackPtr,#0 %% Load return value into $0
                        \t\tTRAP\t0,Halt,0
                                
                        FgetsBuf\tBYTE\t0,0
                        FgetsArgs\tOCTA\tFgetsBuf,2
                        _getChar\tLDA\tTrapReg,FgetsArgs
                        \t\tTRAP\t0,Fgets,StdIn
                        \t\tLDA\tTrapReg,FgetsBuf
                        \t\tLDB\tTrapReg,TrapReg,#0
                        \t\tSTB\tTrapReg,StackPtr,#7
                        \t\tPOP\t0,0
                                
                        _putChar\tLDB\tTrapReg,StackPtr,#0F
                        \t\tSTB\tTrapReg,StackPtr,#8
                        \t\tADD\tTrapReg,StackPtr,#8
                        \t\tTRAP\t0,Fputs,StdOut
                        \t\tPOP\t0,0
                                
                        _new\t\tSTO\tHeapPtr,StackPtr,#0 %% Store HeapPtr in RV
                        \t\tLDO\t$0,StackPtr,8
                        \t\tADD\tHeapPtr,HeapPtr,$0 %% Increase HeapPtr by size
                        \t\tPOP\t0,0
                        
                        AssertFail\tBYTE\t"Assertion failed!",10,0
                        _assert\t\tLDO\tTrapReg,StackPtr,#8
                        \t\tBZ\tTrapReg,Lassert
                        \t\tPOP\t0,0
                        Lassert\t\tLDA\tTrapReg,AssertFail
                        \t\tTRAP\t0,Fputs,StdOut
                        \t\tTRAP\t0,Halt,0
                                
                        _del\t\tPOP\t0,0
                                
                        _exit\t\tTRAP\t0,Halt,0
                        """,
            asm_load_constant(0x7FFFFFFFFFFFFFF8L, "StackPtr"),
            asm_load_constant(
                    0x2000000000000000L + ImcLin.dataChunks().stream().mapToLong(c -> c.size).sum(),
                    "HeapPtr"),
            Compiler.num_regs
        );

        f.println();

        var codes = AsmGen.codes;
        f.printf("%% $$$ Generated code $$$ %%");
        f.printf("%% Code chunks: %d%n%n", codes.size());

        for (var code: codes) {
            var frame = code.frame;
            var instructions = cleaned_core_instructions(code.instrs, code.entryLabel, code.exitLabel);

            f.printf("%% Chunk '%s', depth %d, size %d, locals size %d, args size %d, temps size %d, entry %s, exit %s%n",
                    frame.label.name, frame.depth, frame.size, frame.locsSize,
                    frame.argsSize, code.tempSize, code.entryLabel.name, code.exitLabel.name);

            f.printf("\t\t%% Prologue%n");

            f.print(frame.label.name + asm_constant_parameter(
                    "\t\tSUB\t$0,StackPtr,%s %% SP -= locals + FP + RA%n",
                    frame.locsSize + 8 + 8));
            f.printf("""
                    \t\tSTO\tFramePtr,$0,#8 %% Store FP
                    \t\tGET\tFramePtr,rJ
                    \t\tSTO\tFramePtr,$0,#0 %% Store RA
                    \t\tSET\tFramePtr,StackPtr %% Set new FP
                    \t\tSET\tStackPtr,$0
                    """);

            if (code.tempSize + frame.argsSize > 0) {
                f.print(asm_constant_parameter("\t\tSUB\tStackPtr,StackPtr,%s %% SP -= temps + args%n",
                        code.tempSize + frame.argsSize));
            }


            // Add a jump if first instruction is not the entry label
            if (!(instructions.firstElement() instanceof AsmLABEL label)
                    || !Objects.equals(label.toString(), code.entryLabel.name)) {
                f.printf("\t\tJMP\t%s", code.entryLabel.name);
            }

            if (instructions.lastElement() instanceof AsmOPER oper
                && oper.toString().startsWith("\t\tJMP")
                && oper.jumps().contains(code.exitLabel)) {
                instructions.remove(instructions.size() - 1);
            }


            f.printf("\t\t%% Core%n");

            for (int i = 0; i < instructions.size(); i++) {
                var instruction = instructions.get(i);

                var instr_label = "";
                if (instruction instanceof AsmLABEL label) {
                    if (i + 1 < instructions.size()) {
                        if (!(instructions.get(i + 1) instanceof AsmLABEL)) {
                            instr_label = label.toString();
                            instruction = instructions.get(++i);
                        } else {
                            f.printf("%s\tSWYM%n", label);
                            continue;
                        }
                    } else {
                        break;
                    }
                }

                var instr = instruction.toString(RegAll.tempToReg).replace("$253", "FramePtr");
                f.printf("%s%s%n", instr_label, instr);
            }
            f.printf("\t\t%% Epilogue%n");


            f.printf("%s\t\tSTO\t$%s,FramePtr,#0 %% Store return value%n", code.exitLabel.name, RegAll.tempToReg.get(frame.RV));

            if (code.tempSize + frame.argsSize > 0) {
                f.print(asm_constant_parameter("\t\tADD\tStackPtr,StackPtr,%s %% SP += temps + args%n",
                        code.tempSize + frame.argsSize));
            }


            f.printf("""
                    \t\tLDO\t$0,StackPtr,#0
                    \t\tPUT\trJ,$0 %% Restore RA
                    \t\tLDO\tFramePtr,StackPtr,#8 %% Restore FP
                    """);
            f.print(asm_constant_parameter(
                    "\t\tADD\tStackPtr,StackPtr,%s %% SP += locals + FP + RA%n",
                    frame.locsSize + 8 + 8));

            f.printf("""
            \t\tPOP\t0,0 %% Pop off
            """);

            f.println();
        }
    }

    private static String asm_constant_parameter(String instruction, long parameter) {
        if (parameter >= 0 && parameter < 256) {
            return String.format(instruction, String.format("#%x", parameter));
        }

        return String.format("%s%n%s",
                asm_load_constant(parameter, "\\$0"),
                String.format(instruction, "$0")
        );
    }

    private static String asm_load_constant(long constant, String target_register) {
        var instructions = new Vector<AsmInstr>();
        var reg_map = new HashMap<MemTemp, Integer>();
        reg_map.put(new ImcCONST(constant).accept(new ExprGenerator(), instructions), -1);

        return instructions.stream()
                .map(i -> i.toString(reg_map))
                .collect(Collectors.joining(String.format("%n")))
                .replaceAll("\\$-1", target_register);
    }

    private static Vector<AsmInstr> cleaned_core_instructions(Vector<AsmInstr> asm_instructions, MemLabel entry_label, MemLabel exit_label) {
        var instructions = new Vector<>(asm_instructions);
        var negative_labels = new HashSet<String>();

        // Collect negative labels
        for (var instruction : instructions) {
            if (instruction instanceof AsmOPER oper) {
                var jumps = oper.jumps();
                if (jumps.size() == 2) {
                    negative_labels.add(jumps.lastElement().name);
                }
            }
        }

        // Remove negative labels
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i) instanceof AsmLABEL label) {
                if (negative_labels.contains(label.toString())
                        && !ChunkGenerator.nonremovable_negative_labels.contains(label.toString())) {
                    negative_labels.remove(label.toString());
                    instructions.remove(i--);
                }
            }
        }

        return instructions;
    }
}
