package prev23.data.imc.code.expr;

import prev23.common.logger.*;
import prev23.data.mem.*;
import prev23.data.imc.visitor.*;

/**
 * Temporary variable.
 * 
 * Returns the value of a temporary variable.
 */
public class ImcTEMP extends ImcExpr {

	/** The temporary variable. */
	public final MemTemp temp;

	/**
	 * Constructs a temporary variable.
	 * 
	 * @param temp The temporary variable.
	 */
	public ImcTEMP(MemTemp temp) {
		this.temp = temp;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

	@Override
	public void log(Logger logger) {
		logger.begElement("imc");
		logger.addAttribute("instruction", "TEMP(" + temp.temp + ")");
		logger.endElement();
	}

	@Override
	public String toString() {
		return "TEMP(" + temp.temp + ")";
	}

}
