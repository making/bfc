package am.ik.bf;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import am.ik.bf.expression.Expression;
import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.ExpressionStatement;
import am.ik.bf.statement.InputStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.OutputStatement;
import am.ik.bf.statement.Statement;

public final class BrainfuckParser {

	private final List<BrainfuckToken> tokens;

	public BrainfuckParser(List<BrainfuckToken> tokens) {
		this.tokens = tokens;
	}

	public static List<Statement> parse(String code) {
		final BrainfuckLexer lexer = new BrainfuckLexer(code);
		final List<BrainfuckToken> tokens = lexer.tokenize();
		final BrainfuckParser parser = new BrainfuckParser(tokens);
		return parser.parse();
	}

	public List<Statement> parse() {
		final List<Statement> statements = new ArrayList<>();
		final List<Expression> expressions = new ArrayList<>();
		final Stack<LoopStatement> loopStack = new Stack<>();
		for (BrainfuckToken token : this.tokens) {
			switch (token.type()) {
				case INCREMENT_VALUE -> expressions.add(new IncrementValueExpression(1));
				case DECREMENT_VALUE -> expressions.add(new IncrementValueExpression(-1));
				case INCREMENT_POINTER -> expressions.add(new IncrementPointerExpression(1));
				case DECREMENT_POINTER -> expressions.add(new IncrementPointerExpression(-1));
				case OUTPUT -> {
					this.addRemainingExpressions(statements, loopStack, expressions);
					this.addStatement(statements, loopStack, new OutputStatement());
				}
				case INPUT -> {
					this.addRemainingExpressions(statements, loopStack, expressions);
					this.addStatement(statements, loopStack, new InputStatement());
				}
				case LOOP_START -> {
					this.addRemainingExpressions(statements, loopStack, expressions);
					loopStack.push(new LoopStatement(new ArrayList<>()));
				}
				case LOOP_END -> {
					if (loopStack.isEmpty()) {
						throw new IllegalStateException("no open loop!");
					}
					this.addRemainingExpressions(statements, loopStack, expressions);
					final LoopStatement statement = loopStack.pop();
					this.addStatement(statements, loopStack, statement);
				}
				default -> {
					/* NOOP */
				}
			}
		}
		if (!expressions.isEmpty()) {
			statements.add(new ExpressionStatement(new ArrayList<>(expressions)));
			expressions.clear();
		}
		return statements;
	}

	public void addRemainingExpressions(List<Statement> statements, Stack<LoopStatement> loopStack,
			List<Expression> expressions) {
		if (!expressions.isEmpty()) {
			final ExpressionStatement statement = new ExpressionStatement(List.copyOf(expressions));
			this.addStatement(statements, loopStack, statement);
			expressions.clear();
		}
	}

	private void addStatement(List<Statement> statements, Stack<LoopStatement> loopStack, Statement statement) {
		if (loopStack.isEmpty()) {
			statements.add(statement);
		}
		else {
			loopStack.peek().addStatement(statement);
		}
	}

}
