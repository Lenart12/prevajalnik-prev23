package prev23.data.mem;

import prev23.common.logger.*;

/**
 * An access to a variable at a fixed address.
 * 
 * (Also used for string constants.)
 */
public class MemAbsAccess extends MemAccess {

	/** Label denoting a fixed address. */
	public final MemLabel label;

	/** Initial value. */
	public final String init;

	/**
	 * Constructs a new absolute access.
	 * 
	 * @param size  The size of a variable.
	 * @param label Offset of a variable at an absolute address.
	 * @param init  Initial value (or {@code null}).
	 */
	public MemAbsAccess(long size, MemLabel label, String init) {
		super(size);
		this.label = label;
		this.init = init;
	}

	/**
	 * Constructs a new absolute access.
	 * 
	 * @param size  The size of a variable.
	 * @param label Offset of a variable at an absolute address.
	 */
	public MemAbsAccess(long size, MemLabel label) {
		super(size);
		this.label = label;
		this.init = null;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("access");
		logger.addAttribute("size", Long.toString(size));
		logger.addAttribute("label", label.name);
		if (init != null)
			logger.addAttribute("init", init);
		logger.endElement();
	}

}
