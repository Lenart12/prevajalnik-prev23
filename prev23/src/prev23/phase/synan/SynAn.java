package prev23.phase.synan;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import prev23.common.report.*;
import prev23.data.sym.Token;
import prev23.phase.*;
import prev23.phase.lexan.*;

/**
 * Syntax analysis phase.
 */
public class SynAn extends Phase {
	
	/** The parse tree. */
	public static PrevParser.SourceContext tree;
		
	/** The ANTLR parser that actually performs syntax analysis. */
	public final PrevParser parser;

	/**
	 * Phase construction: sets up logging and the ANTLR lexer and parser.
	 */
	public SynAn(LexAn lexan) {
		super("synan");
		parser = new PrevParser(new CommonTokenStream(lexan.lexer));
		parser.removeErrorListeners();
		parser.addErrorListener(new BaseErrorListener() {
			private String error_context(String rule_name) {
				switch (rule_name) {
					case "source":
					case "decl":
					case "decl1":
					case "some_decl":
						return "";
					case "type_decl":
					case "type_decl1":
					case "type_decl2":
						return "type declaration";
					case "type_ass":
						return "type assignment";
					case "fun_decl":
					case "fun_decl1":
					case "fun_decl2":
						return "function declaration";
					case "fun_def": 
						return "function definition";
					case "fun_args": 
					case "fun_args1": 
					case "fun_args2": 
						return "function arguments";
					case "fun_args_def": 
						return "function arguments definition";
					case "fun_body": 
						return "function body";
					case "var_decl": 
					case "var_decl1": 
					case "var_decl2": 
						return "variable declaration";
					case "var_def": 
						return "variable definition";
					case "type": 
						return "type";
					case "raw_type": 
						return "raw type";
					case "expr_type":
						return "expression type";
					case "record_type": 
					case "record_types": 
					case "record_types1": 
						return "record type";
					case "record_type_def": 
						return "record type definition";
					case "expr": 
					case "disjunctive_expr": 
					case "disjunctive_expr1": 
					case "conjunctive_expr": 
					case "conjunctive_expr1": 
					case "relational_expr": 
					case "relational_expr1": 
					case "additive_expr": 
					case "additive_expr1": 
					case "multiplicative_expr": 
					case "multiplicative_expr1": 
					case "prefix_expr": 
					case "postfix_expr": 
					case "postfix_expr1": 
					case "primary_expr": 
						return "expression";
					case "const_expr":
						return "constant expression";
					case "id_expr":
						return "identifier";
					case "id_expr1":
						return "function call";
					case "id_expr2":
					case "id_expr3":
						return "function call arguments";
					case "mem_expr":
						return "memory management expression";
					case "cast_expr":
						return "expression";
					case "cast_expr1":
						return "cast expression";
					case "statement": 
					case "assign_statement":
						return "statement";
					case "assign_statement1": 
						return "assignment";
					case "if_statement":
						return "if statement";
					case "if_statement1": 
						return "else statement";
					case "while_statement":
						return "while statement";
					case "let_statement":
						return "let statement";
					case "block_statement": 
					case "block_statement1": 
						return "block statement";
					case "disjunctive_op": 
					case "conjunctive_op": 
					case "relational_op": 
					case "additive_op": 
					case "multiplicative_op": 
					case "prefix_op": 
					case "postfix_op": 
						return "operator";
				}
				return rule_name;
			}
		
			private String get_parse_stack(PrevParser parser) {
				String last_context = "";
				StringBuilder sb = new StringBuilder();
				for (final var rule : parser.getRuleInvocationStack()) {
					final var rule_context = error_context(rule);
		
					if (rule_context.length() > 0 && rule_context != last_context) {
						last_context = rule_context;
						sb.append(String.format(" - %s%n", rule_context));
					}
				}
				if (!sb.isEmpty()) sb.insert(0, "Parse stack:\n");
				return sb.toString();
			}

			private String get_top_stack(PrevParser parser) {
				for (final var rule : parser.getRuleInvocationStack()) {
					final var rule_context = error_context(rule);
		
					if (rule_context.length() > 0) {
						return rule_context;
					}
				}
				return "source code";
			}
		
			private void ParseError(PrevParser parser) {
				final var token = parser.getCurrentToken();
				final var text = token.getText();
		
				throw new Report.Error(
					new Location(
						token.getLine(), token.getCharPositionInLine(),
						token.getLine(), token.getCharPositionInLine() + text.length() - 1),
					String.format(
						"Unexpected token [%s] while parsing %s\n%sExpected one of: %s",
						text, get_top_stack(parser), get_parse_stack(parser),
						parser.getExpectedTokens().toString(parser.getVocabulary())
					)
				);
			}

			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
					ParseError((PrevParser) recognizer);
			}
		});
	}

	/**
	 * Logs a parse tree.
	 * 
	 * @param tree Parse tree to be logged.
	 */
	public void log(ParseTree tree) {
		if (logger == null)
			return;
		if (tree instanceof TerminalNodeImpl) {
			TerminalNodeImpl node = (TerminalNodeImpl) tree;
			Token token = (Token) (node.getPayload());
			token.log(logger);
		}
		if (tree instanceof ParserRuleContext) {
			ParserRuleContext node = (ParserRuleContext) tree;
			logger.begElement("nont");
			logger.addAttribute("label", PrevParser.ruleNames[node.getRuleIndex()]);
			int numChildren = node.getChildCount();
			for (int i = 0; i < numChildren; i++)
				log(node.getChild(i));
			logger.endElement();
		}
	}

}
