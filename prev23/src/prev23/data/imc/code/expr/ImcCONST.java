package prev23.data.imc.code.expr;

import prev23.common.logger.*;
import prev23.data.imc.visitor.*;

/**
 * Constant.
 * 
 * Returns the value of a constant.
 */
public class ImcCONST extends ImcExpr {

	/** The value. */
	public final long value;

	/**
	 * Constructs a new constant.
	 * 
	 * @param value The value.
	 */
	public ImcCONST(long value) {
		this.value = value;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

	@Override
	public void log(Logger logger) {
		logger.begElement("imc");
		logger.addAttribute("instruction", toString());
		logger.endElement();
	}

	@Override
	public String toString() {
		return "CONST(" + value + ")";
	}

}
