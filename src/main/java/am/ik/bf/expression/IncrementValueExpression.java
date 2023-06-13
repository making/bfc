package am.ik.bf.expression;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.Evaluator;

public record IncrementValueExpression(int value) implements Expression {
	@Override
	public void evaluate(Evaluator evaluator) {
		evaluator.incrementValue(this.value);
	}

	@Override
	public void generate(CodeGenerator codeGenerator) {
		codeGenerator.generateIncrementValueExpression(this);
	}
}
