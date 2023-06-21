package am.ik.bf.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntConsumer;

import am.ik.bf.BrainfuckCompiler;
import am.ik.bf.BrainfuckInterpreter;
import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.codegen.JavaCodeGenerator;
import am.ik.bf.codegen.JavaScriptCodeGenerator;
import am.ik.bf.codegen.JvmByteCode6Generator;
import am.ik.bf.codegen.WatCodeGenerator;

public class BfcCli {

	private final CliOptions options;

	private final PrintStream out;

	private final PrintStream err;

	private final IntConsumer exiter;

	public BfcCli(CliOptions options, PrintStream out, PrintStream err, IntConsumer exiter) {
		this.options = options;
		this.out = out;
		this.err = err;
		this.exiter = exiter;
	}

	public void run() {
		if (this.options.isEmpty()) {
			this.help();
			this.exiter.accept(1);
			return;
		}
		if (this.options.contains("-v") || this.options.contains("--version")) {
			this.out.println("DEV");
			return;
		}
		if (this.options.contains("-h") || this.options.contains("--help")) {
			this.help();
			return;
		}
		if (!this.options.containsNoKey()) {
			this.err.println("the input file is missing.");
			this.help();
			this.exiter.accept(1);
			return;
		}
		final Path input = Path.of(this.options.getNokey());
		try {
			final String code = Files.readString(input).trim();

			if (this.options.contains("-o")) {
				this.compile(code);
			}
			else {
				this.interpret(code);
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void compile(String code) throws IOException {
		final Path output = Path.of(this.options.get("-o"));
		final CodeGenerator codeGenerator = this.determineCodeGenerator(output);
		final BrainfuckCompiler compiler = new BrainfuckCompiler(codeGenerator);
		compiler.compile(code);
	}

	private void interpret(String code) {
		final BrainfuckInterpreter interpreter = new BrainfuckInterpreter(System.in, System.out);
		interpreter.interpret(code);
	}

	private CodeGenerator determineCodeGenerator(Path output) throws IOException {
		final String fileName = output.getFileName().toString();
		final String[] split = fileName.split("\\.");
		final String ext = split[split.length - 1];
		final String[] classNameArray = new String[split.length - 1];
		System.arraycopy(split, 0, classNameArray, 0, split.length - 1);
		final String className = String.join(".", classNameArray);
		final OutputStream stream = Files.newOutputStream(output);
		return switch (ext) {
			case "java" -> new JavaCodeGenerator(className, stream);
			case "js" -> new JavaScriptCodeGenerator(stream);
			case "class" -> new JvmByteCode6Generator(className, stream);
			case "wat" -> new WatCodeGenerator(stream);
			default -> throw new IllegalArgumentException(ext + " is not supported extension.");
		};
	}

	public void help() {
		this.err.println("""
				Braininf*ck Compiler/Interpreter

				bfc <input file> [options]

				---
				Options:
				-o:		output file name of the compilation (supported extensions: *.js, *.java, *.class, *.wat).
						without this option bfc works as an interpreter.
				-v, --version:	print version
				-h, --help:	print this help
				""");
	}

	public static void main(String[] args) throws Exception {
		final CliOptions options = CliOptions.build(args);
		final BfcCli cli = new BfcCli(options, System.out, System.err, System::exit);
		try {
			cli.run();
		}
		catch (RuntimeException e) {
			System.err.println(e.getMessage());
			cli.help();
		}
	}

}
