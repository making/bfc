package am.ik.bf.statement;

import java.util.List;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.Evaluator;

public record LoopStatement(List<Statement> statements) implements Statement {
	@Override
	public void evaluate(Evaluator runtime) {
		while (runtime.getValue() != 0) {
			this.statements.forEach(expression -> expression.evaluate(runtime));
		}
	}

	@Override
	public void generate(CodeGenerator codeGenerator) {
		codeGenerator.generateLoopStatement(this);
	}

	public void addStatement(Statement statement) {
		this.statements.add(statement);
	}
}
