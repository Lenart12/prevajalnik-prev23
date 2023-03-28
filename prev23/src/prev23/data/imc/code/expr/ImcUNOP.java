package prev23.data.imc.code.expr;

import prev23.common.logger.*;
import prev23.data.imc.visitor.*;

/**
 * Unary operation (logical, arithmetic).
 * 
 * Evaluates the value of the operand, performs the selected unary operation and
 * return its result.
 */
public class ImcUNOP extends ImcExpr {

	public enum Oper {
		NOT, NEG,
	}

	/** The operator. */
	public final Oper oper;

	/** The operand. */
	public final ImcExpr subExpr;

	/**
	 * Constructs a unary operation.
	 * 
	 * @param oper    The operator.
	 * @param subExpr The operand.
	 */
	public ImcUNOP(Oper oper, ImcExpr subExpr) {
		this.oper = oper;
		this.subExpr = subExpr;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

	@Override
	public void log(Logger logger) {
		logger.begElement("imc");
		logger.addAttribute("instruction", "UNOP(" + oper + ")");
		subExpr.log(logger);
		logger.endElement();
	}

	@Override
	public String toString() {
		return "UNOP(" + oper + "," + subExpr.toString() + ")";
	}

}
