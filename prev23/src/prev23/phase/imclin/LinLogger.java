package prev23.phase.imclin;

import prev23.common.logger.*;
import prev23.data.ast.visitor.*;
import prev23.data.imc.code.stmt.*;
import prev23.data.lin.*;

public class LinLogger extends AstNullVisitor<Object, String> {

	/** The logger the log should be written to. */
	private final Logger logger;

	/**
	 * Constructs a new visitor with a logger the log should be written to.
	 * 
	 * @param logger The logger the log should be written to.
	 */
	public LinLogger(Logger logger) {
		this.logger = logger;
	}

	// *** CHUNK LOGGER ***

	public void log(LinDataChunk dataChunk) {
		if (logger == null)
			return;
		logger.begElement("datachunk");
		logger.addAttribute("label", dataChunk.label.name);
		logger.addAttribute("size", Long.toString(dataChunk.size));
		logger.addAttribute("init", dataChunk.init);
		logger.endElement();
	}

	public void log(LinCodeChunk codeChunk) {
		if (logger == null)
			return;
		logger.begElement("codechunk");
		logger.addAttribute("entrylabel", codeChunk.entryLabel.name);
		logger.addAttribute("exitlabel", codeChunk.exitLabel.name);
		codeChunk.frame.log(logger);
		for (ImcStmt stmt : codeChunk.stmts()) {
			logger.begElement("stmt");
			stmt.log(logger);
			logger.endElement();
		}
		logger.endElement();
	}

}
