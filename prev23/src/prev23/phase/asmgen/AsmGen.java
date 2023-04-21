package prev23.phase.asmgen;

import java.util.*;

import prev23.data.imc.code.stmt.*;
import prev23.data.lin.*;
import prev23.data.asm.*;
import prev23.phase.*;
import prev23.phase.imclin.*;

/**
 * Machine code generator.
 */
public class AsmGen extends Phase {

	public static Vector<Code> codes = new Vector<Code>();

	public AsmGen() {
		super("asmgen");
	}

	public void genAsmCodes() {
		for (LinCodeChunk codeChunk : ImcLin.codeChunks()) {
			Code code = genAsmCode(codeChunk);
			codes.add(code);
		}
	}

	public Code genAsmCode(LinCodeChunk codeChunk) {
		Vector<AsmInstr> instrs = new Vector<AsmInstr>();
		for (ImcStmt stmt : codeChunk.stmts()) {
			instrs.addAll(stmt.accept(new StmtGenerator(), null));
		}
		return new Code(codeChunk.frame, codeChunk.entryLabel, codeChunk.exitLabel, instrs);
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
				logger.endElement();
			}
			logger.endElement();
			logger.endElement();
		}
	}

}
