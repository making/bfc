package am.ik.bf.codegen;

import java.io.OutputStream;

import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.InputStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.OutputStatement;

public class JavaScriptCodeGenerator extends PrintStreamCodeGenerator {

	public JavaScriptCodeGenerator(OutputStream out) {
		super(out);
	}

	@Override
	public void begin() {
		this.out.print("""
				const bf = () => {
				  const memory = Array(1024).fill(0);
				  let pointer = 0;
				""");
	}

	@Override
	public void end() {
		this.out.print("""
				};
				bf();
				""");
	}

	@Override
	public void generateLoopStatement(LoopStatement statement) {
		this.out.println("  while (memory[pointer] !== 0) {");
		statement.statements().forEach(s -> s.generate(this));
		this.out.println();
		this.out.println("  }");
	}

	@Override
	public void generateOutputStatement(OutputStatement statement) {
		this.out.print("""
				  process.stdout.write(String.fromCharCode(memory[pointer]));
				""");
	}

	@Override
	public void generateInputStatement(InputStatement statement) {
		this.out.print("""
				  /* TODO */
				""");
	}

	@Override
	public void generateIncrementValueExpression(IncrementValueExpression expression) {
		this.out.printf("""
				  memory[pointer] += %d;
				""", expression.value());
	}

	@Override
	public void generateIncrementPointerExpression(IncrementPointerExpression expression) {
		this.out.printf("""
				  pointer += %d;
				""", expression.value());
	}

}
