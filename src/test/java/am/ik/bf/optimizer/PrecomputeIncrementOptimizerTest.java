package am.ik.bf.optimizer;

import java.util.List;

import am.ik.bf.BrainfuckParser;
import am.ik.bf.expression.Expression;
import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.ExpressionStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.Statement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrecomputeIncrementOptimizerTest {

	final PrecomputeIncrementOptimizer<IncrementPointerExpression> valueOptimizer = new PrecomputeIncrementOptimizer<>(
			IncrementPointerExpression.class, IncrementPointerExpression::new, IncrementPointerExpression::value);

	final PrecomputeIncrementOptimizer<IncrementValueExpression> pointerOptimizer = new PrecomputeIncrementOptimizer<>(
			IncrementValueExpression.class, IncrementValueExpression::new, IncrementValueExpression::value);

	final CompositeOptimizer optimizer = new CompositeOptimizer(List.of(valueOptimizer, pointerOptimizer));

	@Test
	void optimizeValue() {
		final List<Statement> statements = BrainfuckParser.parse(">>>>><<");
		final List<Statement> optimized = valueOptimizer.optimize(statements);
		assertThat(optimized).hasSize(1);
		assertThat(optimized.get(0)).isInstanceOf(ExpressionStatement.class);
		final List<Expression> expressions = ((ExpressionStatement) optimized.get(0)).expressions();
		assertThat(expressions).hasSize(1);
		assertThat(expressions.get(0)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(((IncrementPointerExpression) expressions.get(0)).value()).isEqualTo(3);
	}

	@Test
	void optimizeValueMixed() {
		final List<Statement> statements = BrainfuckParser.parse(">>>+>>-<<");
		final List<Statement> optimized = valueOptimizer.optimize(statements);
		assertThat(optimized).hasSize(1);
		assertThat(optimized.get(0)).isInstanceOf(ExpressionStatement.class);
		final List<Expression> expressions = ((ExpressionStatement) optimized.get(0)).expressions();
		assertThat(expressions).hasSize(5);
		assertThat(expressions.get(0)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(((IncrementPointerExpression) expressions.get(0)).value()).isEqualTo(3);
		assertThat(expressions.get(1)).isInstanceOf(IncrementValueExpression.class);
		assertThat(expressions.get(2)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(((IncrementPointerExpression) expressions.get(2)).value()).isEqualTo(2);
		assertThat(expressions.get(3)).isInstanceOf(IncrementValueExpression.class);
		assertThat(expressions.get(4)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(((IncrementPointerExpression) expressions.get(4)).value()).isEqualTo(-2);
	}

	@Test
	void optimizePointer() {
		final List<Statement> statements = BrainfuckParser.parse("+++++--");
		final List<Statement> optimized = pointerOptimizer.optimize(statements);
		assertThat(optimized).hasSize(1);
		assertThat(optimized.get(0)).isInstanceOf(ExpressionStatement.class);
		final List<Expression> expressions = ((ExpressionStatement) optimized.get(0)).expressions();
		assertThat(expressions).hasSize(1);
		assertThat(expressions.get(0)).isInstanceOf(IncrementValueExpression.class);
		assertThat(((IncrementValueExpression) expressions.get(0)).value()).isEqualTo(3);
	}

	@Test
	void optimizePointerMixed() {
		final List<Statement> statements = BrainfuckParser.parse("+++>++<--");
		final List<Statement> optimized = pointerOptimizer.optimize(statements);
		assertThat(optimized).hasSize(1);
		assertThat(optimized.get(0)).isInstanceOf(ExpressionStatement.class);
		final List<Expression> expressions = ((ExpressionStatement) optimized.get(0)).expressions();
		assertThat(expressions).hasSize(5);
		assertThat(expressions.get(0)).isInstanceOf(IncrementValueExpression.class);
		assertThat(((IncrementValueExpression) expressions.get(0)).value()).isEqualTo(3);
		assertThat(expressions.get(1)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(expressions.get(2)).isInstanceOf(IncrementValueExpression.class);
		assertThat(((IncrementValueExpression) expressions.get(2)).value()).isEqualTo(2);
		assertThat(expressions.get(3)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(expressions.get(4)).isInstanceOf(IncrementValueExpression.class);
		assertThat(((IncrementValueExpression) expressions.get(4)).value()).isEqualTo(-2);
	}

	@Test
	void optimizeInLoop() {
		final List<Statement> statements = BrainfuckParser.parse("[>>>>><<]");
		final List<Statement> optimized = valueOptimizer.optimize(statements);
		assertThat(optimized).hasSize(1);
		assertThat(optimized.get(0)).isInstanceOf(LoopStatement.class);
		final List<Statement> loopStatement = ((LoopStatement) optimized.get(0)).statements();
		assertThat(loopStatement.get(0)).isInstanceOf(ExpressionStatement.class);
		final List<Expression> expressions = ((ExpressionStatement) loopStatement.get(0)).expressions();
		assertThat(expressions).hasSize(1);
		assertThat(expressions.get(0)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(((IncrementPointerExpression) expressions.get(0)).value()).isEqualTo(3);
	}

	@Test
	void optimizeComposite() {
		final List<Statement> statements = BrainfuckParser.parse("+++>>++<<--");
		final List<Statement> optimized = optimizer.optimize(statements);
		assertThat(optimized).hasSize(1);
		assertThat(optimized.get(0)).isInstanceOf(ExpressionStatement.class);
		final List<Expression> expressions = ((ExpressionStatement) optimized.get(0)).expressions();
		assertThat(expressions).hasSize(5);
		assertThat(expressions.get(0)).isInstanceOf(IncrementValueExpression.class);
		assertThat(((IncrementValueExpression) expressions.get(0)).value()).isEqualTo(3);
		assertThat(expressions.get(1)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(((IncrementPointerExpression) expressions.get(1)).value()).isEqualTo(2);
		assertThat(expressions.get(2)).isInstanceOf(IncrementValueExpression.class);
		assertThat(((IncrementValueExpression) expressions.get(2)).value()).isEqualTo(2);
		assertThat(expressions.get(3)).isInstanceOf(IncrementPointerExpression.class);
		assertThat(((IncrementPointerExpression) expressions.get(3)).value()).isEqualTo(-2);
		assertThat(expressions.get(4)).isInstanceOf(IncrementValueExpression.class);
		assertThat(((IncrementValueExpression) expressions.get(4)).value()).isEqualTo(-2);

	}

}