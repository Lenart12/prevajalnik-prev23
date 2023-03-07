package prev23.data.typ;

import prev23.common.logger.*;

/**
 * Array type.
 */
public class SemArr extends SemType {

	/** Element type. */
	public final SemType elemType;

	/** Number of elements. */
	public final long numElems;

	/**
	 * Constructs a new array type.
	 * 
	 * @param elemType The element type.
	 * @param numElems The number of elements.
	 */
	public SemArr(SemType elemType, long numElems) {
		this.elemType = elemType;
		this.numElems = numElems;
	}

	@Override
	public long size() {
		return numElems * elemType.size();
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("semtype");
		logger.addAttribute("type", "ARRAY[" + numElems + "]");
		elemType.log(logger);
		logger.endElement();
	}

}
