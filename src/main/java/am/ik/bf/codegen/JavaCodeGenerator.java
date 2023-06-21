package am.ik.bf.codegen;

import java.io.OutputStream;

import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.InputStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.OutputStatement;

public class JavaCodeGenerator extends PrintStreamCodeGenerator {

	private final String className;

	public JavaCodeGenerator(String className, OutputStream out) {
		super(out);
		this.className = className;
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
						try {
							memory[pointer] = System.in.read();
						}
						catch (java.io.IOException e) {
							throw new java.io.UncheckedIOException(e);
						}
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
