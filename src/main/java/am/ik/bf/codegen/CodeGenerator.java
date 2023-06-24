package am.ik.bf.codegen;

import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.expression.ResetValueExpression;
import am.ik.bf.statement.ExpressionStatement;
import am.ik.bf.statement.InputStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.OutputStatement;

public interface CodeGenerator {

	void begin();

	void end();

	void generateLoopStatement(LoopStatement statement);

	void generateOutputStatement(OutputStatement statement);

	void generateInputStatement(InputStatement statement);

	void generateIncrementValueExpression(IncrementValueExpression expression);

	void generateIncrementPointerExpression(IncrementPointerExpression expression);

	void generateSetValueToZeroExpression(ResetValueExpression expression);

	default void generateExpressionStatement(ExpressionStatement statement) {
		statement.expressions().forEach(expression -> expression.generate(this));
	}

}
