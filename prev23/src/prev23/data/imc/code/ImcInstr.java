package prev23.data.imc.code;

import prev23.common.logger.*;
import prev23.data.imc.visitor.*;

/**
 * Intermediate code instruction.
 */
public abstract class ImcInstr implements Loggable {

	public abstract <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg);

}
