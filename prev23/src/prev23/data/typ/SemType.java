package prev23.data.typ;

import prev23.common.logger.*;

/**
 * An abstract class for representing types.
 */
public abstract class SemType implements Loggable {

	/**
	 * Returns the actual type (not a synonym).
	 * 
	 * @return The actual type (not a synonym).
	 */
	public SemType actualType() {
		return this;
	}

	/**
	 * Returns the size of values of this type.
	 * 
	 * @return The size of values of this type.
	 */
	public abstract long size();

}
