package am.ik.bf.codegen;

import java.io.OutputStream;
import java.io.PrintStream;

import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.InputStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.OutputStatement;

public class JavaCodeGenerator implements CodeGenerator {

	private final String className;

	private final PrintStream out;

	public JavaCodeGenerator(String className, OutputStream out) {
		this.className = className;
		this.out = new PrintStream(out);
	}

	@Override
	public void begin() {
		this.out.printf("""
				public class %s {

					public static void main(String[] args) {
						final int[] memory = new int[1024];
						int pointer = 0;
				""", this.className);
	}

	@Override
	public void end() {
		this.out.print("""
					}
				}
				""");
	}

	@Override
	public void generateLoopStatement(LoopStatement statement) {
		this.out.println("	while (memory[pointer] != 0) {");
		statement.statements().forEach(s -> s.generate(this));
		this.out.println();
		this.out.println("	}");
	}

	@Override
	public void generateOutputStatement(OutputStatement statement) {
		this.out.print("""
						System.out.print((char) memory[pointer]);
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
