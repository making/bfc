package am.ik.bf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import am.ik.bf.codegen.CodeGenerator;
import am.ik.bf.codegen.JavaCodeGenerator;
import am.ik.bf.codegen.JavaScriptCodeGenerator;
import am.ik.bf.codegen.JvmByteCode6Generator;
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

	public static void compileToJavaScript(String code, OutputStream out) {
		final BrainfuckCompiler compiler = new BrainfuckCompiler(new JavaScriptCodeGenerator(out));
		compiler.compile(code);
	}

	public static void compileToJavaScript(String code) {
		compileToJavaScript(code, System.out);
	}

	public static void compileToJava(String code, OutputStream out) {
		final BrainfuckCompiler compiler = new BrainfuckCompiler(new JavaCodeGenerator(out));
		compiler.compile(code);
	}

	public static void compileToJava(String code) {
		compileToJava(code, System.out);
	}

	public static void compileToJvmByteCode(String code) {
		compileToJvmByteCode(code, System.out);
	}

	public static void compileToJvmByteCode(String code, OutputStream out) {
		final BrainfuckCompiler compiler = new BrainfuckCompiler(new JvmByteCode6Generator(out));
		compiler.compile(code);
	}

	public static void main(String[] args) throws IOException {
		String code = "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.";
		// BrainfuckCompiler.compileToJava(code,
		// Files.newOutputStream(Path.of("target/Bf.java")));
		// BrainfuckCompiler.compileToJavaScript(code,
		// Files.newOutputStream(Path.of("target/bf.js")));

		// BrainfuckCompiler.compileToJavaScript("++.", System.out);
		// BrainfuckCompiler.compileToJava("++.", System.out);
		// String code = "++[--]";
		BrainfuckCompiler.compileToJvmByteCode(code, Files.newOutputStream(Path.of("target/HelloWorld.class")));
		// BrainfuckCompiler.compileToJava(code);
	}

}
