package prev23.data.imc.code.expr;

import prev23.common.logger.*;
import prev23.data.imc.visitor.*;

/**
 * Binary operation (logical, relational, and arithmetic).
 * 
 * Evaluates the first subexpression, evaluates the second expression, performs
 * the selected binary operation and returns its result.
 */
public class ImcBINOP extends ImcExpr {

	public enum Oper {
		OR, AND, EQU, NEQ, LTH, GTH, LEQ, GEQ, ADD, SUB, MUL, DIV, MOD,
	}

	/** The operator. */
	public final Oper oper;

	/** The first operand. */
	public final ImcExpr fstExpr;

	/** The second operand. */
	public final ImcExpr sndExpr;

	/**
	 * Constructs a new binary operation.
	 * 
	 * @param oper    The operator.
	 * @param fstExpr The first operand.
	 * @param sndExpr The second operand.
	 */
	public ImcBINOP(Oper oper, ImcExpr fstExpr, ImcExpr sndExpr) {
		this.oper = oper;
		this.fstExpr = fstExpr;
		this.sndExpr = sndExpr;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

	@Override
	public void log(Logger logger) {
		logger.begElement("imc");
		logger.addAttribute("instruction", "BINOP(" + oper + ")");
		fstExpr.log(logger);
		sndExpr.log(logger);
		logger.endElement();
	}

	@Override
	public String toString() {
		return "BINOP(" + oper + "," + fstExpr.toString() + "," + sndExpr.toString() + ")";
	}

}
