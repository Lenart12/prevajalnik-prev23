package prev23;

import java.util.*;

import prev23.common.report.*;
import prev23.phase.lexan.*;
import prev23.phase.synan.*;
import prev23.phase.abstr.*;
import prev23.phase.seman.*;

/**
 * The compiler.
 * 
 * The compiler should be run as
 * 
 * <p>
 * <code>$ java prev.Compiler </code><i>command-line-arguments...</i>
 * </p>
 * 
 * The following command line arguments are available:
 * 
 * <ul>
 * <li><code>--src-file-name</code>: The name of the source file, i.e., the file
 * containing the code to be compiled.</li>
 * <li><code>--dst-file-name</code>: The name of the destination file, i.e., the
 * file containing the compiled code.</li>
 * </ul>
 * 
 * The source file can be specified by its name only, i.e., without
 * <code>--src-file-name</code>.
 * 
 * @author bostjan.slivnik@fri.uni-lj.si
 */
public class Compiler {

	/** (Unused but included to keep javadoc happy.) */
	private Compiler() {
		throw new Report.InternalError();
	}

	// COMMAND LINE ARGUMENTS

	/** All valid phases of the compiler. */
	private static final String phases = "none|lexan|synan|abstr|seman";

	/** Values of command line arguments indexed by their command line switch. */
	private static HashMap<String, String> cmdLineArgs = new HashMap<String, String>();

	/**
	 * Returns the value of a command line argument.
	 *
	 * @param cmdLineArgName Command line argument name.
	 * @return Command line argument value.
	 */
	public static String cmdLineArgValue(String cmdLineArgName) {
		return cmdLineArgs.get(cmdLineArgName);
	}

	// THE COMPILER'S STARTUP METHOD

	/**
	 * The compiler.
	 * 
	 * @param args Command line arguments (see {@link prev23.Compiler}).
	 */
	public static void main(String[] args) {
		try {
			Report.info("This is PREV'23 compiler:");

			// Scan the command line.
			for (int argc = 0; argc < args.length; argc++) {
				if (args[argc].startsWith("--")) {
					// Command line argument.
					String cmdLineArgName = args[argc].replaceFirst("=.*", "");
					String cmdLineArgValue = args[argc].replaceFirst("^[^=]*=", "");
					if (cmdLineArgs.get(cmdLineArgName) == null) {
						cmdLineArgs.put(cmdLineArgName, cmdLineArgValue);
					} else {
						Report.warning("Command line argument '" + args[argc] + "' ignored.");
						continue;
					}
				} else {
					// Source file name.
					if (cmdLineArgValue("--src-file-name") == null) {
						cmdLineArgs.put("--src-file-name", args[argc]);
					} else {
						Report.warning("Source file '" + args[argc] + "' ignored.");
					}
				}
			}
			// Check the command line arguments.
			if (cmdLineArgs.get("--src-file-name") == null) {
				throw new Report.Error("Source file not specified.");
			}
			if (cmdLineArgs.get("--dst-file-name") == null) {
				cmdLineArgs.put("--dst-file-name",
						cmdLineArgs.get("--src-file-name").replaceFirst("\\.[^./]*$", "") + ".mms");
			}
			if ((cmdLineArgs.get("--target-phase") == null) || (cmdLineArgs.get("--target-phase").equals("all"))) {
				cmdLineArgs.put("--target-phase", phases.replaceFirst("^.*\\|", ""));
			}

			// Compilation process carried out phase by phase.
			while (true) {

				// Lexical analysis.
				if (cmdLineArgs.get("--target-phase").equals("lexan"))
					try (LexAn lexan = new LexAn()) {
						while (lexan.lexer.nextToken().getType() != prev23.data.sym.Token.EOF) {
						}
						break;
					}

				// Syntax analysis.
				try (LexAn lexan = new LexAn(); SynAn synan = new SynAn(lexan)) {
					SynAn.tree = synan.parser.source();
					synan.log(SynAn.tree);
				}
				if (Compiler.cmdLineArgValue("--target-phase").equals("synan"))
					break;

				// Abstract syntax tree construction.
				try (Abstr abstr = new Abstr()) {
					Abstr.tree = SynAn.tree.ast;
					AbsLogger logger = new AbsLogger(abstr.logger);
					Abstr.tree.accept(logger, null);
				}
				if (Compiler.cmdLineArgValue("--target-phase").equals("abstr"))
					break;

				// Semantic analysis.
				try (SemAn seman = new SemAn()) {
					Abstr.tree.accept(new NameResolver(), null);
					AbsLogger logger = new AbsLogger(seman.logger);
					logger.addSubvisitor(new SemLogger(seman.logger));
					Abstr.tree.accept(logger, null);
				}
				if (Compiler.cmdLineArgValue("--target-phase").equals("seman"))
					break;

			}

			Report.info("Done.");
		} catch (Report.Error __) {
			System.exit(1);
		}
	}

}
