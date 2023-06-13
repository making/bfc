package am.ik.bf.statement;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.Evaluator;

public interface Statement {

	void evaluate(Evaluator runtime);

	void generate(CodeGenerator codeGenerator);

}
