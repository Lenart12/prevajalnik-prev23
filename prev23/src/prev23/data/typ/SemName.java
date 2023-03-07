package prev23.data.typ;

import prev23.common.logger.*;
import prev23.common.report.*;

/**
 * Named type.
 */
public class SemName extends SemType {

	/** Name. */
	public final String name;

	/** Type this named type represents. */
	private SemType type = null;

	/**
	 * Constructs a new named type.
	 * 
	 * @param name The type this named type represents.
	 */
	public SemName(String name) {
		this.name = name;
	}

	/**
	 * Defines the type this named type represents.
	 * 
	 * @param typeThe type this named type represents.
	 */
	public void define(SemType type) {
		if (this.type != null)
			throw new Report.InternalError();
		this.type = type;
	}

	/**
	 * Returns the type this named type represents.
	 * 
	 * @return The type this named type represents.
	 */
	public SemType type() {
		return type;
	}

	/**
	 * Returns the actual type (not a synonym).
	 * 
	 * @return The actual type (not a synonym).
	 */
	@Override
	public SemType actualType() {
		return type.actualType();
	}

	@Override
	public long size() {
		return type.size();
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("semtype");
		logger.addAttribute("type", "TYPE(" + (type != null ? name : "") + ")");
		logger.endElement();
	}

}
