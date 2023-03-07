package prev23.data.typ;

import prev23.common.logger.*;

/**
 * Type {@code integer}.
 */
public class SemInt extends SemType {

	@Override
	public long size() {
		return 8;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("semtype");
		logger.addAttribute("type", "INTEGER");
		logger.endElement();
	}

}
