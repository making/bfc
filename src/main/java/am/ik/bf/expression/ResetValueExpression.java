package am.ik.bf.expression;

import am.ik.bf.Evaluator;
import am.ik.bf.codegen.CodeGenerator;

public class ResetValueExpression implements Expression {

	@Override
	public void evaluate(Evaluator evaluator) {
		evaluator.setValue(0);
	}

	@Override
	public void generate(CodeGenerator codeGenerator) {
		codeGenerator.generateSetValueToZeroExpression(this);
	}

}
