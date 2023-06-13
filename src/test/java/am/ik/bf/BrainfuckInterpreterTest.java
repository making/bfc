package am.ik.bf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrainfuckInterpreterTest {

	@Test
	void interpretA() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final BrainfuckInterpreter interpreter = new BrainfuckInterpreter(System.in, new PrintStream(out));
		interpreter.interpret("++++++++[>++++++++<-]>+.");
		assertThat(out.toString(StandardCharsets.UTF_8)).isEqualTo("A");
	}

	@Test
	void interpretHelloWorld() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final BrainfuckInterpreter interpreter = new BrainfuckInterpreter(System.in, new PrintStream(out));
		interpreter.interpret(
				"++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.");
		assertThat(out.toString(StandardCharsets.UTF_8)).isEqualTo("Hello World");
	}

	@Test
	void interpretInOut() {
		final ByteArrayInputStream in = new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8));
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final BrainfuckInterpreter interpreter = new BrainfuckInterpreter(in, new PrintStream(out));
		interpreter.interpret(",.,.,.,.,.");
		assertThat(out.toString(StandardCharsets.UTF_8)).isEqualTo("hello");
	}

}