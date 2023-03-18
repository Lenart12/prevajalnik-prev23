package prev23.data.typ;

import java.util.*;

import prev23.common.logger.*;

/**
 * Record type.
 */
public class SemRec extends SemType {

	/** Component types. */
	private final SemType[] cmpTypes;
	public final HashMap<String, SemType> cmpNames;
	public SemRec(Collection<SemType> compTypes, List<String> compNames) {
		assert compTypes.size() == compNames.size();
		this.cmpTypes = new SemType[compTypes.size()];
		this.cmpNames = new LinkedHashMap<>();
		int index = 0;
		for (SemType compType : compTypes) {
			this.cmpTypes[index] = compType;
			this.cmpNames.put(compNames.get(index++), compType);
		}
	}

	/**
	 * Returns the type of the specified component.
	 * 
	 * @param idx The index of the component
	 * @return The type of the component.
	 */
	public SemType cmpType(int idx) {
		return cmpTypes[idx];
	}

	/**
	 * Returns the number of components.
	 * 
	 * @return The number of components.
	 */
	public int numCmps() {
		return cmpTypes.length;
	}

	@Override
	public long size() {
		long size = 0;
		for (int index = 0; index < cmpTypes.length; index++) {
			size += cmpTypes[index].size();
		}
		return size;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("semtype");
		logger.addAttribute("type", "RECORD");
		for (SemType compType : cmpTypes)
			compType.log(logger);
		logger.endElement();
	}

}
