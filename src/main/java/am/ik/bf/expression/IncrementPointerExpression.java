package am.ik.bf.expression;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.Evaluator;

public record IncrementPointerExpression(int value) implements Expression {
	@Override
	public void evaluate(Evaluator evaluator) {
		evaluator.incrementPointer(this.value);
	}

	@Override
	public void generate(CodeGenerator codeGenerator) {
		codeGenerator.generateIncrementPointerExpression(this);
	}
}
