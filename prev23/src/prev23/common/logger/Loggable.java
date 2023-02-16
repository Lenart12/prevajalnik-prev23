package prev23.common.logger;

/**
 * Implemented by classes that a log should be produced for.
 * 
 * @author bostjan.slivnik@fri.uni-lj.si
 */
public interface Loggable {

	/**
	 * Generates a log of this object.
	 * 
	 * @param logger The logger a log of this object is generated to.
	 */
	public void log(Logger logger);

}
