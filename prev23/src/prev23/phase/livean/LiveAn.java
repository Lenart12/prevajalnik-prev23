package prev23.phase.livean;

import prev23.data.mem.*;
import prev23.data.asm.*;
import prev23.phase.*;
import prev23.phase.asmgen.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * Liveness analysis.
 */
public class LiveAn extends Phase {

	public LiveAn() {
		super("livean");
	}

	public static void analyze_instructions(Vector<AsmInstr> instructions, MemLabel exit_label) {
		var labels = new HashMap<String, AsmInstr>();

		for (var instruction : instructions) {
			if (instruction instanceof AsmOPER operation) {
				operation.removeAllFromIn();
				operation.removeAllFromOut();
			}
			if (instruction instanceof AsmLABEL label) {
				labels.put(label.toString(), instruction);
			}
		}

		var finished = true;
		do {
			finished = true;

			for (int i = 0; i < instructions.size(); i++) {
				var instruction = (AsmOPER) instructions.get(i);
				var new_in = new HashSet<>(instruction.uses());
				var old_out = instruction.out();
				instruction.defs().forEach(old_out::remove);
				new_in.addAll(old_out);

				var new_out = new HashSet<MemTemp>();

				if (!instruction.jumps().isEmpty() && !instruction.toString().startsWith("\t\tPUSHJ")) {
					for (var jump : instruction.jumps()) {
						if (jump == exit_label) continue;
						new_out.addAll(labels.get(jump.name).in());
					}
				} else if (i < instructions.size() - 1) {
					new_out.addAll(instructions.get(i + 1).in());
				}

				if (!new_in.equals(instruction.in())) {
					instruction.removeAllFromIn();
					instruction.addInTemps(new_in);
					finished = false;
				}

				if (!new_out.equals(instruction.out())) {
					instruction.removeAllFromOut();
					instruction.addOutTemp(new_out);
					finished = false;
				}
			}
		} while (!finished);
	}

	public void analysis() {
		for (var chunk : AsmGen.codes) {
			analyze_instructions(chunk.instrs, chunk.exitLabel);
		}
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
				logger.addAttribute("code", instr.toString());
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
