package am.ik.bf.codegen;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.InputStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.OutputStatement;
import am.ik.jvm.AccessFlag;
import am.ik.jvm.ArrayType;
import am.ik.jvm.ByteCodeWriter;
import am.ik.jvm.ConstantPool;
import am.ik.jvm.ConstantPool.ClassConstant;
import am.ik.jvm.ConstantPool.FieldrefConstant;
import am.ik.jvm.ConstantPool.MethodrefConstant;
import am.ik.jvm.ConstantPool.NameAndTypeConstant;
import am.ik.jvm.ConstantPool.Utf8Constant;
import am.ik.jvm.Opcode;

public class JvmByteCode6Generator implements CodeGenerator {

	private final OutputStream out;

	private ByteCodeWriter byteCodeWriter;

	private final List<Integer> code = new ArrayList<>();

	private final ConstantPool constantPool = new ConstantPool();

	private final Utf8Constant javaLangObjectUtf8 = constantPool.addUtf8("java/lang/Object");

	private final Utf8Constant javaLangSystemUtf8 = constantPool.addUtf8("java/lang/System");

	private final Utf8Constant javaLangPrintStreamUtf8 = constantPool.addUtf8("java/io/PrintStream");

	private final Utf8Constant outUtf8 = constantPool.addUtf8("out");

	private final Utf8Constant printUtf8 = constantPool.addUtf8("print");

	private final Utf8Constant javaLangPrintStreamType = constantPool.addUtf8("Ljava/io/PrintStream;");

	private final Utf8Constant javaLangStringArrayType = constantPool.addUtf8("([Ljava/lang/String;)V");

	private final Utf8Constant charType = constantPool.addUtf8("(C)V");

	private final Utf8Constant codeUtf8 = constantPool.addUtf8("Code");

	private final Utf8Constant mainUtf8 = constantPool.addUtf8("main");

	private final ClassConstant targetClass;

	private final ClassConstant javaLangObjectClass = constantPool.addClass(javaLangObjectUtf8);

	private final ClassConstant javaLangSystemClass = constantPool.addClass(javaLangSystemUtf8);

	private final ClassConstant javaLangPrintStreamClass = constantPool.addClass(javaLangPrintStreamUtf8);

	private final NameAndTypeConstant systemOutField = constantPool.addNameAndType(outUtf8, javaLangPrintStreamType);

	private final NameAndTypeConstant printMethod = constantPool.addNameAndType(printUtf8, charType);

	private final FieldrefConstant systemOutFieldRef = constantPool.addFieldref(javaLangSystemClass, systemOutField);

	private final MethodrefConstant printMethodRef = constantPool.addMethodref(javaLangPrintStreamClass, printMethod);

	public JvmByteCode6Generator(String className, OutputStream out) {
		this.out = out;
		this.targetClass = constantPool.addClass(constantPool.addUtf8(className));
	}

	@Override
	public void begin() {
		this.byteCodeWriter = new ByteCodeWriter(this.out) //
			.write(0xca, 0xfe, 0xba, 0xbe) // cafebabe
			.writeVersion(0, 50) // Java 6
			.writeConstantPool(constantPool) //
			.writeClass(AccessFlag.ACC_PUBLIC, targetClass, javaLangObjectClass) //
			.writeInterfaces(interfaces -> {
			})
			.writeFields(fields -> {
			});
		this.code.addAll(List.of( //
				Opcode.SIPUSH, 0x04, 0x00, /* 1024 */ //
				Opcode.NEWARRAY, ArrayType.T_INT, Opcode.ASTORE_1, // memory
				Opcode.ICONST_0, Opcode.ISTORE_2 // pointer
		));
	}

	@Override
	public void end() {
		this.code.add(Opcode.RETURN);
		this.byteCodeWriter //
			.writeMethods(methods -> methods.add(AccessFlag.ACC_PUBLIC + AccessFlag.ACC_STATIC,
					mainUtf8 /* main */,
					javaLangStringArrayType /* ([Ljava/lang/String;)V */,
					method -> method.writeAttributes(attributes -> attributes.add(codeUtf8,
							attribute -> attribute.writeU2(4) // max_stack
								.writeU2(3) // max_locals
								.writeCode((Object[]) code.toArray(code.toArray(new Integer[0])))
								.writeU2(0) // exception_table_length
								.writeU2(0) // attributes_count
					)))) //
			.writeAttributes(attributesDef -> {
			});
	}

	@Override
	public void generateLoopStatement(LoopStatement statement) {
		this.code.addAll(List.of( //
				Opcode.ALOAD_1, // memory
				Opcode.ILOAD_2, // pointer
				Opcode.IALOAD, // memory[pointer]
				Opcode.IFEQ //
		));
		this.writeBytes(toU2(0)); // replace later
		final int indexToReplace = this.code.size() - 2;
		final int beforeSize = this.code.size();
		statement.statements().forEach(s -> s.generate(this));
		final int indexIfeq = this.code.size() - beforeSize + 6;
		final byte[] ifeq = toU2(indexIfeq);
		for (int i = 0; i < ifeq.length; i++) {
			this.code.set(indexToReplace + i, (int) ifeq[i]);
		}
		this.code.add(Opcode.GOTO);
		this.writeBytes(toU2(-indexIfeq));
	}

	@Override
	public void generateOutputStatement(OutputStatement statement) {
		this.code.add(Opcode.GETSTATIC);
		this.writeBytes(systemOutFieldRef.indexAsU2() /* out */);
		this.code.addAll(List.of( //
				Opcode.ALOAD_1, // memory
				Opcode.ILOAD_2, // pointer
				Opcode.IALOAD, // memory[pointer]
				Opcode.INVOKEVIRTUAL //
		));
		this.writeBytes(printMethodRef.indexAsU2() /* print */);
	}

	@Override
	public void generateInputStatement(InputStatement statement) {
	}

	@Override
	public void generateIncrementValueExpression(IncrementValueExpression expression) {
		this.code.addAll(List.of( //
				Opcode.ALOAD_1, // memory
				Opcode.ILOAD_2, // pointer
				Opcode.DUP2, //
				Opcode.IALOAD, // memory[pointer]
				Opcode.ICONST_1, // 1
				expression.value() > 0 ? Opcode.IADD /* + */ : Opcode.ISUB /* - */, //
				Opcode.IASTORE //
		));
	}

	@Override
	public void generateIncrementPointerExpression(IncrementPointerExpression expression) {
		this.code.addAll(List.of( //
				Opcode.IINC, //
				0x02, // pointer
				expression.value() > 0 ? 0x01 /* 1 */ : 0xff /* -1 */ //
		));
	}

	private void writeBytes(byte[] bytes) {
		for (byte b : bytes) {
			this.code.add((int) b);
		}
	}

	private static byte[] toU2(int i) {
		return ByteBuffer.allocate(2).putShort((short) i).array();
	}

}
