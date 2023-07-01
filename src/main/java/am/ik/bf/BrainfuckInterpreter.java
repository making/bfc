package am.ik.bf;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.List;

import am.ik.bf.statement.Statement;

public final class BrainfuckInterpreter implements Evaluator {

	private final int[] memory;

	public int pointer = 0;

	private final InputStream in;

	private final PrintStream out;

	private final BrainfuckOptimizer optimizer;

	public BrainfuckInterpreter(InputStream in, PrintStream out, BrainfuckOptimizer optimizer, int memorySize) {
		this.in = in;
		this.out = out;
		this.optimizer = optimizer;
		this.memory = new int[memorySize];
	}

	public BrainfuckInterpreter(InputStream in, PrintStream out, BrainfuckOptimizer optimizer) {
		this(in, out, optimizer, 10240);
	}

	public BrainfuckInterpreter(InputStream in, PrintStream out) {
		this(in, out, null);
	}

	public BrainfuckInterpreter() {
		this(System.in, System.out, null);
	}

	public void interpret(String code) {
		List<Statement> statements = BrainfuckParser.parse(code);
		if (this.optimizer != null) {
			statements = this.optimizer.optimize(statements);
		}
		this.interpret(statements);
	}

	public void interpret(List<Statement> statements) {
		statements.forEach(statement -> statement.evaluate(this));
	}

	@Override
	public int getValue() {
		return this.memory[this.pointer];
	}

	@Override
	public void incrementValue(int value) {
		this.memory[this.pointer] += value;
	}

	@Override
	public void setValue(int value) {
		this.memory[this.pointer] = value;
	}

	@Override
	public int readValue() {
		try {
			return this.in.read();
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void incrementPointer(int value) {
		this.pointer += value;
		if (this.pointer >= this.memory.length) {
			throw new IllegalStateException("buffer overflow");
		}
	}

	@Override
	public void printValue() {
		this.out.print((char) this.getValue());
	}

}
