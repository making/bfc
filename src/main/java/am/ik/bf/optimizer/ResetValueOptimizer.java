package am.ik.bf.optimizer;

import java.util.List;

import am.ik.bf.BrainfuckOptimizer;
import am.ik.bf.expression.Expression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.expression.ResetValueExpression;
import am.ik.bf.statement.ExpressionStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.Statement;

public class ResetValueOptimizer implements BrainfuckOptimizer {

	@Override
	public List<Statement> optimize(List<Statement> statements) {
		return statements.stream().map(s -> s instanceof LoopStatement ? this.optimize((LoopStatement) s) : s).toList();
	}

	public Statement optimize(LoopStatement loopStatement) {
		final List<Statement> statements = loopStatement.statements();
		if (statements.size() == 1) {
			final Statement statement = statements.get(0);
			if (statement instanceof ExpressionStatement) {
				final List<Expression> expressions = ((ExpressionStatement) statement).expressions();
				if (expressions.size() == 1) {
					final Expression expression = expressions.get(0);
					if (expression instanceof IncrementValueExpression) {
						final int value = ((IncrementValueExpression) expression).value();
						if (value == -1) {
							return new ExpressionStatement(List.of(new ResetValueExpression()));
						}
					}
				}
			}
		}
		return loopStatement;
	}

}
