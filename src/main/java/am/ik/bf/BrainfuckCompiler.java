package am.ik.bf;

import java.util.List;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.statement.Statement;

public class BrainfuckCompiler {

	private final CodeGenerator codeGenerator;

	public BrainfuckCompiler(CodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
	}

	public void compile(String code) {
		final List<Statement> statements = BrainfuckParser.parse(code);
		this.codeGenerator.begin();
		for (Statement statement : statements) {
			statement.generate(this.codeGenerator);
		}
		this.codeGenerator.end();
	}

}
