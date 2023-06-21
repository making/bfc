package am.ik.bf.codegen;

import java.io.OutputStream;
import java.io.PrintStream;

public abstract class PrintStreamCodeGenerator implements CodeGenerator {

	protected final PrintStream out;

	protected PrintStreamCodeGenerator(OutputStream out) {
		this.out = new PrintStream(out);
	}

}
