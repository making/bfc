package am.ik.bf.optimizer;

import java.util.List;

import am.ik.bf.BrainfuckParser;
import am.ik.bf.expression.Expression;
import am.ik.bf.expression.ResetValueExpression;
import am.ik.bf.statement.ExpressionStatement;
import am.ik.bf.statement.Statement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResetValueOptimizerTest {

	private final ResetValueOptimizer optimizer = new ResetValueOptimizer();

	@Test
	void optimize() {
		final List<Statement> statements = BrainfuckParser.parse("++[-]");
		final List<Statement> optimize = optimizer.optimize(statements);
		assertThat(optimize).hasSize(2);
		final Statement statement = optimize.get(1);
		assertThat(statement).isInstanceOf(ExpressionStatement.class);
		final List<Expression> expressions = ((ExpressionStatement) statement).expressions();
		assertThat(expressions).hasSize(1);
		final Expression expression = expressions.get(0);
		assertThat(expression).isInstanceOf(ResetValueExpression.class);
	}

}