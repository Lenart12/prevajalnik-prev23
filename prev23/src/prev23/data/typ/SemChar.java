package prev23.data.typ;

import prev23.common.logger.*;

/**
 * Type {@code char}.
 */
public class SemChar extends SemType {

	@Override
	public long size() {
		return 8;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("semtype");
		logger.addAttribute("type", "CHAR");
		logger.endElement();
	}

}
