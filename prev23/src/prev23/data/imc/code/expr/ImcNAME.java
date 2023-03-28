package prev23.data.imc.code.expr;

import prev23.common.logger.*;
import prev23.data.mem.*;
import prev23.data.imc.visitor.*;

/**
 * Name.
 * 
 * Returns the address that the label is mapped to.
 */
public class ImcNAME extends ImcExpr {

	/** The label. */
	public final MemLabel label;

	/**
	 * Constructs a new name.
	 * 
	 * @param label The label.
	 */
	public ImcNAME(MemLabel label) {
		this.label = label;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

	@Override
	public void log(Logger logger) {
		logger.begElement("imc");
		logger.addAttribute("instruction", "NAME(" + label.name + ")");
		logger.endElement();
	}

	@Override
	public String toString() {
		return "NAME(" + label.name + ")";
	}

}
