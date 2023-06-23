package am.ik.bf.optimizer;

import java.util.List;

import am.ik.bf.BrainfuckOptimizer;
import am.ik.bf.statement.Statement;

public class CompositeOptimizer implements BrainfuckOptimizer {

	private final List<BrainfuckOptimizer> optimizers;

	public CompositeOptimizer(List<BrainfuckOptimizer> optimizers) {
		this.optimizers = optimizers;
	}

	@Override
	public List<Statement> optimize(List<Statement> statements) {
		List<Statement> optimized = statements;
		for (BrainfuckOptimizer optimizer : this.optimizers) {
			optimized = optimizer.optimize(optimized);
		}
		return optimized;
	}

}
