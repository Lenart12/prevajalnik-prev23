package prev23.phase.imclin;

import java.util.*;
import prev23.common.report.*;
import prev23.data.mem.*;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.code.stmt.*;
import prev23.data.imc.visitor.*;
import prev23.data.lin.*;

/**
 * Interpreter - for testing purposes only.
 */
public class Interpreter {

	private boolean debug = false;

	private Random random;

	private HashMap<Long, Byte> memory;

	private HashMap<MemTemp, Long> temps;

	private HashMap<MemLabel, Long> dataMemLabels;

	private HashMap<MemLabel, Integer> jumpMemLabels;

	private HashMap<MemLabel, LinCodeChunk> callMemLabels;

	private MemTemp SP;

	private MemTemp FP;

	private MemTemp RV;

	private MemTemp HP;

	public Interpreter(Vector<LinDataChunk> dataChunks, Vector<LinCodeChunk> codeChunks) {
		random = new Random();

		this.memory = new HashMap<Long, Byte>();
		this.temps = new HashMap<MemTemp, Long>();

		SP = new MemTemp();
		tempST(SP, 0x7FFFFFFFFFFFFFF8l);
		HP = new MemTemp();
		tempST(HP, 0x2000000000000000l);

		this.dataMemLabels = new HashMap<MemLabel, Long>();
		for (LinDataChunk dataChunk : dataChunks) {
			if (debug) {
				System.out.printf("### %s @ %d\n", dataChunk.label.name, tempLD(HP, false));
			}
			this.dataMemLabels.put(dataChunk.label, tempLD(HP, false));
			if (dataChunk.init != null) {
				for (int c = 0; c < dataChunk.init.length() - 2; c++)
					memST(tempLD(HP, false) + 8 * c, (long) dataChunk.init.charAt(c + 1), false);
				memST(tempLD(HP, false) + 8 * (dataChunk.init.length() - 2), 0L, false);
			}
			tempST(HP, tempLD(HP, false) + dataChunk.size, debug);
		}
		if (debug)
			System.out.printf("###\n");

		this.jumpMemLabels = new HashMap<MemLabel, Integer>();
		this.callMemLabels = new HashMap<MemLabel, LinCodeChunk>();
		for (LinCodeChunk codeChunk : codeChunks) {
			this.callMemLabels.put(codeChunk.frame.label, codeChunk);
			Vector<ImcStmt> stmts = codeChunk.stmts();
			for (int stmtOffset = 0; stmtOffset < stmts.size(); stmtOffset++) {
				if (stmts.get(stmtOffset) instanceof ImcLABEL)
					jumpMemLabels.put(((ImcLABEL) stmts.get(stmtOffset)).label, stmtOffset);
			}
		}
	}

	private void memST(Long address, Long value) {
		memST(address, value, debug);
	}

	private void memST(Long address, Long value, boolean debug) {
		if (debug)
			System.out.printf("### [%d] <- %d\n", address, value);
		for (int b = 0; b <= 7; b++) {
			long longval = value % 0x100;
			byte byteval = (byte) longval;
			memory.put(address + b, byteval);
			value = value >> 8;
		}
	}

	private Long memLD(Long address) {
		return memLD(address, debug);
	}

	private Long memLD(Long address, boolean debug) {
		Long value = 0L;
		for (int b = 7; b >= 0; b--) {
			Byte byteval = memory.get(address + b);
			if (byteval == null) {
				byteval = (byte) (random.nextLong() / 0x100);
				// throw new Report.Error("INTERPRETER: Uninitialized memory location " +
				// (address + b) + ".");
			}
			long longval = (long) byteval;
			value = (value * 0x100) + (longval < 0 ? longval + 0x100 : longval);
		}
		if (debug)
			System.out.printf("### %d <- [%d]\n", value, address);
		return value;
	}

	private void tempST(MemTemp temp, Long value) {
		tempST(temp, value, debug);
	}

	private void tempST(MemTemp temp, Long value, boolean debug) {
		temps.put(temp, value);
		if (debug) {
			if (temp == SP) {
				System.out.printf("### SP <- %d\n", value);
				return;
			}
			if (temp == FP) {
				System.out.printf("### FP <- %d\n", value);
				return;
			}
			if (temp == RV) {
				System.out.printf("### RV <- %d\n", value);
				return;
			}
			if (temp == HP) {
				System.out.printf("### HP <- %d\n", value);
				return;
			}
			System.out.printf("### T%d <- %d\n", temp.temp, value);
			return;
		}
	}

	private Long tempLD(MemTemp temp) {
		return tempLD(temp, debug);
	}

	private Long tempLD(MemTemp temp, boolean debug) {
		Long value = temps.get(temp);
		if (value == null) {
			value = random.nextLong();
			throw new Report.Error("Uninitialized temporary variable T" + temp.temp + ".");
		}
		if (debug) {
			if (temp == SP) {
				System.out.printf("### %d <- SP\n", value);
				return value;
			}
			if (temp == FP) {
				System.out.printf("### %d <- FP\n", value);
				return value;
			}
			if (temp == RV) {
				System.out.printf("### %d <- RV\n", value);
				return value;
			}
			if (temp == HP) {
				System.out.printf("### %d <- HP\n", value);
				return value;
			}
			System.out.printf("### %d <- T%d\n", value, temp.temp);
			return value;
		}
		return value;
	}

	private class ExprInterpreter implements ImcVisitor<Long, Object> {

		@Override
		public Long visit(ImcBINOP imcBinop, Object arg) {
			Long fstExpr = imcBinop.fstExpr.accept(this, null);
			Long sndExpr = imcBinop.sndExpr.accept(this, null);
			switch (imcBinop.oper) {
			case OR:
				return (fstExpr != 0) | (sndExpr != 0) ? 1L : 0L;
			case AND:
				return (fstExpr != 0) & (sndExpr != 0) ? 1L : 0L;
			case EQU:
				return (fstExpr == sndExpr) ? 1L : 0L;
			case NEQ:
				return (fstExpr != sndExpr) ? 1L : 0L;
			case LEQ:
				return (fstExpr <= sndExpr) ? 1L : 0L;
			case GEQ:
				return (fstExpr >= sndExpr) ? 1L : 0L;
			case LTH:
				return (fstExpr < sndExpr) ? 1L : 0L;
			case GTH:
				return (fstExpr > sndExpr) ? 1L : 0L;
			case ADD:
				return fstExpr + sndExpr;
			case SUB:
				return fstExpr - sndExpr;
			case MUL:
				return fstExpr * sndExpr;
			case DIV:
				return fstExpr / sndExpr;
			case MOD:
				return fstExpr % sndExpr;
			}
			throw new Report.InternalError();
		}

		@Override
		public Long visit(ImcCALL imcCall, Object arg) {
			throw new Report.InternalError();
		}

		@Override
		public Long visit(ImcCONST imcConst, Object arg) {
			return imcConst.value;
		}

		@Override
		public Long visit(ImcMEM imcMem, Object arg) {
			return memLD(imcMem.addr.accept(this, null));
		}

		@Override
		public Long visit(ImcNAME imcName, Object arg) {
			return dataMemLabels.get(imcName.label);
		}

		@Override
		public Long visit(ImcSEXPR imcSExpr, Object arg) {
			throw new Report.InternalError();
		}

		@Override
		public Long visit(ImcTEMP imcMemTemp, Object arg) {
			return tempLD(imcMemTemp.temp);
		}

		@Override
		public Long visit(ImcUNOP imcUnop, Object arg) {
			Long subExpr = imcUnop.subExpr.accept(this, null);
			switch (imcUnop.oper) {
			case NOT:
				return (subExpr == 0) ? 1L : 0L;
			case NEG:
				return -subExpr;
			}
			throw new Report.InternalError();
		}

	}

	private class StmtInterpreter implements ImcVisitor<MemLabel, Object> {

		@Override
		public MemLabel visit(ImcCJUMP imcCJump, Object arg) {
			if (debug)
				System.out.println(imcCJump);
			Long cond = imcCJump.cond.accept(new ExprInterpreter(), null);
			return (cond != 0) ? imcCJump.posLabel : imcCJump.negLabel;
		}

		@Override
		public MemLabel visit(ImcESTMT imcEStmt, Object arg) {
			if (debug)
				System.out.println(imcEStmt);
			if (imcEStmt.expr instanceof ImcCALL) {
				call((ImcCALL) imcEStmt.expr);
				return null;
			}
			imcEStmt.expr.accept(new ExprInterpreter(), null);
			return null;
		}

		@Override
		public MemLabel visit(ImcJUMP imcJump, Object arg) {
			if (debug)
				System.out.println(imcJump);
			return imcJump.label;
		}

		@Override
		public MemLabel visit(ImcLABEL imcMemLabel, Object arg) {
			if (debug)
				System.out.println(imcMemLabel);
			return null;
		}

		@Override
		public MemLabel visit(ImcMOVE imcMove, Object arg) {
			if (debug)
				System.out.println(imcMove);
			if (imcMove.dst instanceof ImcMEM) {
				Long dst = ((ImcMEM) (imcMove.dst)).addr.accept(new ExprInterpreter(), null);
				Long src;
				if (imcMove.src instanceof ImcCALL) {
					call((ImcCALL) imcMove.src);
					src = memLD(tempLD(SP));
				} else
					src = imcMove.src.accept(new ExprInterpreter(), null);
				memST(dst, src);
				return null;
			}
			if (imcMove.dst instanceof ImcTEMP) {
				ImcTEMP dst = (ImcTEMP) (imcMove.dst);
				Long src;
				if (imcMove.src instanceof ImcCALL) {
					call((ImcCALL) imcMove.src);
					src = memLD(tempLD(SP));
				} else
					src = imcMove.src.accept(new ExprInterpreter(), null);
				tempST(dst.temp, src);
				return null;
			}
			throw new Report.InternalError();
		}

		@Override
		public MemLabel visit(ImcSTMTS imcStmts, Object arg) {
			if (debug)
				System.out.println(imcStmts);
			throw new Report.InternalError();
		}

		private void call(ImcCALL imcCall) {
			Long offset = 0L;
			for (ImcExpr callArg : imcCall.args) {
				Long callValue = callArg.accept(new ExprInterpreter(), null);
				memST(tempLD(SP) + offset, callValue);
				offset += 8;
			}
			if (imcCall.label.name.equals("_new")) {
				Long size = memLD(tempLD(SP, false) + 1 * 8, false);
				Long addr = tempLD(HP);
				tempST(HP, addr + size);
				memST(tempLD(SP), addr, false);
				return;
			}
			if (imcCall.label.name.equals("_del")) {
				return;
			}
			if (imcCall.label.name.equals("_exit")) {
				System.exit(1);
			}
			if (imcCall.label.name.equals("_putChar")) {
				Long c = memLD(tempLD(SP, false) + 1 * 8, false);
				System.out.printf("%c", (char) ((long) c) % 0x100);
				return;
			}
			if (imcCall.label.name.equals("_getChar")) {
				char c = '\n';
				try {
					c = (char) System.in.read();
				} catch (Exception __) {
				}
				memST(tempLD(SP), (long) c, false);
				return;
			}
			funCall(imcCall.label);
		}

	}

	public void funCall(MemLabel entryMemLabel) {

		HashMap<MemTemp, Long> storedMemTemps;
		MemTemp storedFP = null;
		MemTemp storedRV = null;

		LinCodeChunk chunk = callMemLabels.get(entryMemLabel);
		MemFrame frame = chunk.frame;
		Vector<ImcStmt> stmts = chunk.stmts();
		int stmtOffset;

		/* PROLOGUE */
		{
			if (debug)
				System.out.printf("###\n### CALL: %s\n", entryMemLabel.name);

			// Store registers and FP.
			storedMemTemps = temps;
			temps = new HashMap<MemTemp, Long>(temps);
			// Store RA.
			// Create a stack frame.
			FP = frame.FP;
			RV = frame.RV;
			tempST(frame.FP, tempLD(SP));
			tempST(SP, tempLD(SP) - frame.size);
			// Jump to the body.
			stmtOffset = jumpMemLabels.get(chunk.entryLabel);
		}

		/* BODY */
		{
			int pc = 0;
			MemLabel label = null;

			while (label != chunk.exitLabel) {
				if (debug) {
					pc++;
					System.out.printf("### %s (%d):\n", chunk.frame.label.name, pc);
					if (pc == 1000000)
						break;
				}

				if (label != null) {
					Integer offset = jumpMemLabels.get(label);
					if (offset == null)
						throw new Report.InternalError();
					stmtOffset = offset;
				}

				label = stmts.get(stmtOffset).accept(new StmtInterpreter(), null);

				stmtOffset += 1;
			}
		}

		/* EPILOGUE */
		{
			// Store the result.
			memST(tempLD(frame.FP), tempLD(frame.RV));
			// Destroy a stack frame.
			tempST(SP, tempLD(SP) + frame.size);
			// Restore registers and FP.
			FP = storedFP;
			RV = storedRV;
			Long hp = tempLD(HP);
			temps = storedMemTemps;
			tempST(HP, hp);
			// Restore RA.
			// Return.

			if (debug)
				System.out.printf("### RETURN: %s\n###\n", entryMemLabel.name);
		}

	}

	public long run(String entryMemLabel) {
		for (MemLabel label : callMemLabels.keySet()) {
			if (label.name.equals(entryMemLabel)) {
				funCall(label);
				return memLD(tempLD(SP));
			}
		}
		throw new Report.InternalError();
	}

}
