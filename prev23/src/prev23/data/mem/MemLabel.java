package prev23.data.mem;

/**
 * A label.
 */
public class MemLabel {

	/** The name of a label. */
	public final String name;

	/** Counter of anonymous labels. */
	private static long count = 0;

	/** Creates a new anonymous label. */
	public MemLabel() {
		this.name = "L" + count;
		count++;
	}

	/**
	 * Creates a new named label.
	 * 
	 * @param name The name of a label.
	 */
	public MemLabel(String name) {
		this.name = "_" + name;
	}

}
