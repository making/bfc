package am.ik.jvm;

import am.ik.jvm.ConstantPool.ClassConstant;
import am.ik.jvm.ConstantPool.FieldrefConstant;
import am.ik.jvm.ConstantPool.MethodrefConstant;
import am.ik.jvm.ConstantPool.Utf8Constant;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class ByteCodeWriterTest {

	@Test
	void createHelloWorldClass(@TempDir Path tempDir) throws Exception {
		// Create a simple Hello World class file using ByteCodeWriter
		String className = "HelloWorld";
		Path classFile = tempDir.resolve(className + ".class");
		try (FileOutputStream fos = new FileOutputStream(classFile.toFile())) {
			createHelloWorldClassFile(className, fos);
		}
		// Verify the class file was created
		assertThat(classFile).exists();
		if (RuntimeDetector.isNativeImage()) {
			System.out.println("Skipping dynamic class loading in native image");
			return;
		}
		// In native image, we cannot load the class dynamically
		// Load and execute the class
		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { tempDir.toUri().toURL() })) {
			Class<?> clazz = classLoader.loadClass(className);
			Method mainMethod = clazz.getMethod("main", String[].class);
			// Capture stdout
			ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
			System.setOut(new PrintStream(capturedOutput));
			try {
				mainMethod.invoke(null, (Object) new String[0]);
			}
			finally {
				System.setOut(System.out);
			}
			// Verify output
			assertThat(capturedOutput.toString()).isEqualTo("Hello World");
		}
	}

	private void createHelloWorldClassFile(String className, FileOutputStream out) {
		ConstantPool constantPool = new ConstantPool();

		// Add constants to pool
		ClassConstant thisClass = constantPool.addClass(constantPool.addUtf8(className));
		ClassConstant superClass = constantPool.addClass(constantPool.addUtf8("java/lang/Object"));

		// System.out field reference
		ClassConstant systemClass = constantPool.addClass(constantPool.addUtf8("java/lang/System"));
		FieldrefConstant systemOutField = constantPool.addFieldref(systemClass, constantPool
			.addNameAndType(constantPool.addUtf8("out"), constantPool.addUtf8("Ljava/io/PrintStream;")));

		// PrintStream.print method reference
		ClassConstant printStreamClass = constantPool.addClass(constantPool.addUtf8("java/io/PrintStream"));
		MethodrefConstant printMethod = constantPool.addMethodref(printStreamClass, constantPool
			.addNameAndType(constantPool.addUtf8("print"), constantPool.addUtf8("(Ljava/lang/String;)V")));

		// String constant "Hello World"
		Utf8Constant helloStringUtf8 = constantPool.addUtf8("Hello World");
		ConstantPool.StringConstant helloString = constantPool.addString(helloStringUtf8);

		// Method names
		Utf8Constant mainMethodName = constantPool.addUtf8("main");
		Utf8Constant mainMethodType = constantPool.addUtf8("([Ljava/lang/String;)V");
		Utf8Constant codeAttributeName = constantPool.addUtf8("Code");

		// Write class file
		new ByteCodeWriter(out).write(0xca, 0xfe, 0xba, 0xbe) // Magic number
			.writeVersion(0, 61) // Java 17
			.writeConstantPool(constantPool)
			.writeClass(AccessFlag.ACC_PUBLIC, thisClass, superClass)
			.writeInterfaces(interfaces -> {
			})
			.writeFields(fields -> {
			})
			.writeMethods(methods -> methods.add(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_STATIC, mainMethodName,
					mainMethodType, method -> method.writeAttributes(attributes -> {
						attributes.add(codeAttributeName, attribute -> {
							attribute.writeU2(2) // max_stack
								.writeU2(1) // max_locals
								.writeCode(Opcode.GETSTATIC, systemOutField.indexAsU2(), Opcode.LDC,
										(byte) helloString.index(), Opcode.INVOKEVIRTUAL, printMethod.indexAsU2(),
										Opcode.RETURN)
								.writeU2(0) // exception_table_length
								.writeAttributes(attrs -> {
								}); // no more attributes
						});
					})))
			.writeAttributes(attributes -> {
			});
	}

}