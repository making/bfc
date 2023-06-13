package am.ik.bf.statement;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.Evaluator;

public record OutputStatement() implements Statement {
	@Override
	public void evaluate(Evaluator runtime) {
		runtime.printValue();
	}

	@Override
	public void generate(CodeGenerator codeGenerator) {
		codeGenerator.generateOutputStatement(this);
	}
}
