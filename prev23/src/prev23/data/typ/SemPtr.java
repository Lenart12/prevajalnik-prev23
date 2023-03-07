package prev23.data.typ;

import prev23.common.logger.*;

/**
 * Pointer type.
 */
public class SemPtr extends SemType {

	/** Base type. */
	public final SemType baseType;

	/**
	 * Constructs a new pointer type.
	 * 
	 * @param baseType The base type.
	 */
	public SemPtr(SemType baseType) {
		this.baseType = baseType;
	}

	@Override
	public long size() {
		return 8;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("semtype");
		logger.addAttribute("type", "PTR");
		baseType.log(logger);
		logger.endElement();
	}

}
