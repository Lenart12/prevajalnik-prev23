package prev23.phase.regall;

import java.util.*;
import java.util.function.Function;

import prev23.common.report.Report;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.code.stmt.ImcMOVE;
import prev23.data.mem.*;
import prev23.data.asm.*;
import prev23.phase.*;
import prev23.phase.asmgen.*;
import prev23.phase.livean.LiveAn;

/**
 * Register allocation.
 */
public class RegAll extends Phase {

	/** Mapping of temporary variables to registers. */
	public final HashMap<MemTemp, Integer> tempToReg = new HashMap<MemTemp, Integer>();

	public RegAll() {
		super("regall");
	}

	public void allocate() {
		for (var code : AsmGen.codes) {
			tempToReg.put(code.frame.FP, 253);

			int i = 0;
			var max_spills = code.instrs.size();
			while (i++ <= max_spills) {
				var graph = new AllocationGraph();

				var fp = code.frame.FP;
				graph.get_node(code.frame.RV);
				for (var instruction : code.instrs) {
					graph.set_all_pairs(instruction.in(), fp);
					graph.set_all_pairs(instruction.out(), fp);
				}

				if (!graph.try_allocate()) {
					fix_spill(graph.get_spilled_nodes(), code);
				} else {
					graph.fill_temp_to_reg(tempToReg);
					break;
				}
			}

			if (i > max_spills) {
				throw new Report.InternalError();
			}


			code.instrs.removeIf(instr -> instr instanceof AsmMOVE mov
				&& Objects.equals(tempToReg.get(mov.uses().firstElement()), tempToReg.get(mov.defs().firstElement())
			));
		}
	}

	private Vector<AsmInstr> spill_temp(MemTemp to_spill, boolean is_load, Vector<MemTemp> temps, ImcMEM access) {
		if (temps.contains(to_spill)) {
			var new_temp = new MemTemp();
			var imc_temp = new ImcTEMP(new_temp);

			var move = is_load ? new ImcMOVE(imc_temp, access) : new ImcMOVE(access, imc_temp);
			temps.replaceAll(temp -> temp == to_spill ? new_temp : temp);

			return move.accept(new StmtGenerator(), null);
		}
		return new Vector<>();
	}

	private void fix_spill(HashSet<MemTemp> to_spill, Code code) {
		for (var spilled : to_spill) {
			code.tempSize += 8;
			var offset = new ImcCONST(- /*SL*/ 8 - code.frame.locsSize - code.tempSize - /*reservation*/8);

			var access = new ImcMEM(new ImcBINOP(ImcBINOP.Oper.ADD, new ImcTEMP(code.frame.FP), offset));

			var spilled_instructions = new Vector<AsmInstr>();

			for (var _instruction : code.instrs) {
				var instruction = (AsmOPER)_instruction;

				if (!instruction.uses().contains(spilled) && !instruction.defs().contains(spilled)) {
					spilled_instructions.add(instruction);
					continue;
				}

				var uses = instruction.uses();
				var defs = instruction.defs();
				var new_instruction = new AsmOPER(instruction.instr(), uses, defs, instruction.jumps());

				spilled_instructions.addAll(spill_temp(spilled, true, uses, access));
				spilled_instructions.add(new_instruction);
				spilled_instructions.addAll(spill_temp(spilled, false, defs, access));
			}

			code.instrs.clear();
			code.instrs.addAll(spilled_instructions);
		}

		LiveAn.analyze_instructions(code.instrs, code.exitLabel);
	}


	public void log() {
		if (logger == null)
			return;
		for (Code code : AsmGen.codes) {
			logger.begElement("code");
			logger.addAttribute("entrylabel", code.entryLabel.name);
			logger.addAttribute("exitlabel", code.exitLabel.name);
			logger.addAttribute("tempsize", Long.toString(code.tempSize));
			code.frame.log(logger);
			logger.begElement("instructions");
			for (AsmInstr instr : code.instrs) {
				logger.begElement("instruction");
				logger.addAttribute("code", instr.toString(tempToReg));
				logger.begElement("temps");
				logger.addAttribute("name", "use");
				for (MemTemp temp : instr.uses()) {
					logger.begElement("temp");
					logger.addAttribute("name", temp.toString());
					logger.endElement();
				}
				logger.endElement();
				logger.begElement("temps");
				logger.addAttribute("name", "def");
				for (MemTemp temp : instr.defs()) {
					logger.begElement("temp");
					logger.addAttribute("name", temp.toString());
					logger.endElement();
				}
				logger.endElement();
				logger.begElement("temps");
				logger.addAttribute("name", "in");
				for (MemTemp temp : instr.in()) {
					logger.begElement("temp");
					logger.addAttribute("name", temp.toString());
					logger.endElement();
				}
				logger.endElement();
				logger.begElement("temps");
				logger.addAttribute("name", "out");
				for (MemTemp temp : instr.out()) {
					logger.begElement("temp");
					logger.addAttribute("name", temp.toString());
					logger.endElement();
				}
				logger.endElement();
				logger.endElement();
			}
			logger.endElement();
			logger.endElement();
		}
	}

}
