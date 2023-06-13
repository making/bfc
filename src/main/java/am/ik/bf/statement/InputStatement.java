package am.ik.bf.statement;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.Evaluator;

public record InputStatement() implements Statement {
	@Override
	public void evaluate(Evaluator runtime) {
		final int input = runtime.readValue();
		runtime.setValue(input);
	}

	@Override
	public void generate(CodeGenerator codeGenerator) {
		codeGenerator.generateInputStatement(this);
	}

}
