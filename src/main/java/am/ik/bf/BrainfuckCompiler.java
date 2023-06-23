package am.ik.bf;

import java.util.List;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.statement.Statement;

public class BrainfuckCompiler {

	private final CodeGenerator codeGenerator;

	private final BrainfuckOptimizer optimizer;

	public BrainfuckCompiler(CodeGenerator codeGenerator, BrainfuckOptimizer optimizer) {
		this.codeGenerator = codeGenerator;
		this.optimizer = optimizer;
	}

	public void compile(String code) {
		List<Statement> statements = BrainfuckParser.parse(code);
		if (this.optimizer != null) {
			statements = this.optimizer.optimize(statements);
		}
		this.codeGenerator.begin();
		for (Statement statement : statements) {
			statement.generate(this.codeGenerator);
		}
		this.codeGenerator.end();
	}

}
