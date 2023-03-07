package prev23.data.typ;

import prev23.common.logger.*;

/**
 * Type {@code boolean}.
 */
public class SemBool extends SemType {

	@Override
	public long size() {
		return 8;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("semtype");
		logger.addAttribute("type", "BOOLEAN");
		logger.endElement();
	}

}
