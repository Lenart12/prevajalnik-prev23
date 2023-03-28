package prev23.data.imc.code.stmt;

import prev23.common.logger.*;
import prev23.data.imc.code.expr.*;
import prev23.data.imc.visitor.*;

/**
 * Expression statement.
 * 
 * Evaluates expression and throws the result away.
 */
public class ImcESTMT extends ImcStmt {

	/** The expression. */
	public final ImcExpr expr;

	/**
	 * Constructs an expression statement.
	 * 
	 * @param expr The expression.
	 */
	public ImcESTMT(ImcExpr expr) {
		this.expr = expr;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

	@Override
	public void log(Logger logger) {
		logger.begElement("imc");
		logger.addAttribute("instruction", "ESTMT");
		expr.log(logger);
		logger.endElement();
	}

	@Override
	public String toString() {
		return "ESTMT(" + expr.toString() + ")";
	}

}
