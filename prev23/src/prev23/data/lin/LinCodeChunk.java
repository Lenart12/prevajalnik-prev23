package prev23.data.lin;

import java.util.*;

import prev23.data.mem.*;
import prev23.data.imc.code.stmt.*;

/**
 * A chuck of code.
 */
public class LinCodeChunk {

	/** A frame of a function. */
	public final MemFrame frame;

	/** The statements of a function body. */
	private final Vector<ImcStmt> stmts;

	/**
	 * The function's body entry label, i.e., the label the prologue jumps to.
	 */
	public final MemLabel entryLabel;

	/**
	 * The function's body exit label, i.e., the label at which the epilogue starts.
	 */
	public final MemLabel exitLabel;

	/**
	 * Constructs a new code chunk.
	 * 
	 * @param frame      A frame of a function.
	 * @param stmts      The statements of a function body.
	 * @param entryLabel The function's body entry label, i.e., the label the
	 *                   prologue jumps to.
	 * @param exitLabel  The function's body exit label, i.e., the label at which
	 *                   the epilogue starts.
	 */
	public LinCodeChunk(MemFrame frame, Vector<ImcStmt> stmts, MemLabel entryLabel, MemLabel exitLabel) {
		this.frame = frame;
		this.stmts = new Vector<ImcStmt>(stmts);
		this.entryLabel = entryLabel;
		this.exitLabel = exitLabel;
	}

	/**
	 * Returns the statements of a function body.
	 * 
	 * @return The statements of a function body.
	 */
	public Vector<ImcStmt> stmts() {
		return new Vector<ImcStmt>(stmts);
	}

}
