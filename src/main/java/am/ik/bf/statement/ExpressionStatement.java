package am.ik.bf.statement;

import java.util.List;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.Evaluator;
import am.ik.bf.expression.Expression;

public record ExpressionStatement(List<Expression> expressions) implements Statement {
	@Override
	public void evaluate(Evaluator runtime) {
		this.expressions.forEach(expression -> expression.evaluate(runtime));
	}

	@Override
	public void generate(CodeGenerator codeGenerator) {
		codeGenerator.generateExpressionStatement(this);
	}
}
