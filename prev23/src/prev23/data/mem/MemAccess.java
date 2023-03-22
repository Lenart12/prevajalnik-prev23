package prev23.data.mem;

import prev23.common.logger.*;

/**
 * Access to a variable.
 */
public abstract class MemAccess implements Loggable {

	/** The size of the variable. */
	public final long size;

	/**
	 * Creates a new access to a variable.
	 * 
	 * @param size The size of the variable.
	 */
	public MemAccess(long size) {
		this.size = size;
	}

}
