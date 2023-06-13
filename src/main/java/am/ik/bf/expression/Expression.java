package am.ik.bf.expression;

import am.ik.bf.Evaluator;
import am.ik.bf.codegen.CodeGenerator;

public interface Expression {

	void evaluate(Evaluator evaluator);

	void generate(CodeGenerator codeGenerator);

}
