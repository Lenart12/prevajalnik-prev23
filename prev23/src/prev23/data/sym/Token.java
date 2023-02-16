package prev23.data.sym;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;

import prev23.common.logger.*;
import prev23.common.report.*;
import prev23.phase.lexan.*;

/**
 * A customized token that is 'locatable' and 'loggable'.
 */
@SuppressWarnings("serial")
public class Token extends CommonToken implements Locatable, Loggable {

	/** The location of this token. */
	private final Location location;

	public Token(int type, String text) {
		super(type, text);
		setLine(0);
		setCharPositionInLine(0);
		location = new Location(getLine(), getCharPositionInLine(), getLine(),
				getCharPositionInLine() + getText().length() - 1);
	}

	public Token(Pair<TokenSource, CharStream> source, int type, int channel, int start, int stop) {
		super(source, type, channel, start, stop);
		setCharPositionInLine(getCharPositionInLine() - getText().length() + 1);
		location = new Location(getLine(), getCharPositionInLine(), getLine(),
				getCharPositionInLine() + getText().length() - 1);
	}

	@Override
	public Location location() {
		return location;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("term");
		if (getType() == -1) {
			logger.addAttribute("token", "EOF");
			logger.addAttribute("lexeme", "");
		} else {
			logger.addAttribute("token", PrevLexer.VOCABULARY.getSymbolicName(getType()));
			logger.addAttribute("lexeme", getText());
			location.log(logger);
		}
		logger.endElement();
	}

}
