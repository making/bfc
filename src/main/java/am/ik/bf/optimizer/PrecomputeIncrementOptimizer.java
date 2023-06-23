package am.ik.bf.optimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

import am.ik.bf.BrainfuckOptimizer;
import am.ik.bf.expression.Expression;
import am.ik.bf.statement.ExpressionStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.Statement;

public class PrecomputeIncrementOptimizer<T extends Expression> implements BrainfuckOptimizer {

	private final Class<T> clazz;

	private final IntFunction<T> constructor;

	private final ToIntFunction<T> valueSupplier;

	public PrecomputeIncrementOptimizer(Class<T> clazz, IntFunction<T> constructor, ToIntFunction<T> valueSupplier) {
		this.clazz = clazz;
		this.constructor = constructor;
		this.valueSupplier = valueSupplier;
	}

	@Override
	public List<Statement> optimize(List<Statement> statements) {
		return statements.stream()
			.map(s -> s instanceof LoopStatement ? new LoopStatement(this.optimize(((LoopStatement) s).statements()))
					: s)
			.map(s -> s instanceof ExpressionStatement ? this.optimize((ExpressionStatement) s) : s)
			.toList();
	}

	ExpressionStatement optimize(ExpressionStatement statement) {
		final List<Expression> optimized = new ArrayList<>();
		Expression previous = null;
		for (Expression expression : statement.expressions()) {
			if (this.clazz.isInstance(previous) && this.clazz.isInstance(expression)) {
				final int a = this.valueSupplier.applyAsInt(this.clazz.cast(previous));
				final int b = this.valueSupplier.applyAsInt(this.clazz.cast(expression));
				previous = this.constructor.apply(a + b);
			}
			else if (previous != null) {
				optimized.add(previous);
				previous = expression;
			}
			if (previous == null) {
				previous = expression;
			}
		}
		if (previous != null) {
			optimized.add(previous);
		}
		return new ExpressionStatement(optimized);
	}

}
