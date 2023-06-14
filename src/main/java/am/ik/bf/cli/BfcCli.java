package am.ik.bf.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntConsumer;

import am.ik.bf.BrainfuckCompiler;
import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.codegen.JavaCodeGenerator;
import am.ik.bf.codegen.JavaScriptCodeGenerator;
import am.ik.bf.codegen.JvmByteCode6Generator;

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
		final Path input = this.options.get("-i")
			.map(Path::of)
			.orElseThrow(() -> new IllegalArgumentException("the required option '-i' is missing."));
		final Path output = this.options.get("-o")
			.map(Path::of)
			.orElseThrow(() -> new IllegalArgumentException("the required option '-o' is missing."));
		try {
			final String code = Files.readString(input).trim();
			final CodeGenerator codeGenerator = this.determineCodeGenerator(output);
			this.compile(codeGenerator, code);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void compile(CodeGenerator codeGenerator, String code) {
		final BrainfuckCompiler compiler = new BrainfuckCompiler(codeGenerator);
		compiler.compile(code);
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
			default -> throw new IllegalArgumentException(ext + " is not supported extension.");
		};
	}

	public void help() {
		this.err.println("""
				Braininf*ck Compiler

				bfc -i <input file> -o <output file> [options]

				---
				Options:
				-i:		input bf file name
				-o:		output file name (supported extensions: *.js, *.java, *.class)
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

		// String code =
		// "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.";
		// BrainfuckCompiler.compileToJava(code,
		// Files.newOutputStream(Path.of("target/Bf.java")));
		// BrainfuckCompiler.compileToJavaScript(code,
		// Files.newOutputStream(Path.of("target/bf.js")));

		// BrainfuckCompiler.compileToJavaScript("++.", System.out);
		// BrainfuckCompiler.compileToJava("++.", System.out);
		// String code = "++[--]";
		// BrainfuckCompiler.compileToJvmByteCode6(code,
		// Files.newOutputStream(Path.of("target/HelloWorld.class")));
		// BrainfuckCompiler.compileToJava(code);
	}

}