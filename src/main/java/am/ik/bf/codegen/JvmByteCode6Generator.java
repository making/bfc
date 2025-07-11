package am.ik.bf.codegen;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.expression.ResetValueExpression;
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
import am.ik.jvm.ConstantPool.Utf8Constant;
import am.ik.jvm.Opcode;

public class JvmByteCode6Generator implements CodeGenerator {

	private final OutputStream out;

	private final List<Integer> code = new ArrayList<>();

	private final ConstantPool constantPool = new ConstantPool();

	private final ClassConstant targetClass;

	private final List<Integer> branchTargets = new ArrayList<>();

	private final ClassConstant javaLangSystemClass = constantPool.addClass(constantPool.addUtf8("java/lang/System"));

	private final FieldrefConstant systemOutFieldRef = constantPool.addFieldref(javaLangSystemClass,
			constantPool.addNameAndType(constantPool.addUtf8("out"), constantPool.addUtf8("Ljava/io/PrintStream;")));

	private final FieldrefConstant systemInFieldRef = constantPool.addFieldref(javaLangSystemClass,
			constantPool.addNameAndType(constantPool.addUtf8("in"), constantPool.addUtf8("Ljava/io/InputStream;")));

	private final MethodrefConstant printMethodRef = constantPool.addMethodref(
			constantPool.addClass(constantPool.addUtf8("java/io/PrintStream")),
			constantPool.addNameAndType(constantPool.addUtf8("print"), constantPool.addUtf8("(C)V")));

	private final MethodrefConstant readMethodRef = constantPool.addMethodref(
			constantPool.addClass(constantPool.addUtf8("java/io/InputStream")),
			constantPool.addNameAndType(constantPool.addUtf8("read"), constantPool.addUtf8("()I")));

	public JvmByteCode6Generator(String className, OutputStream out) {
		this.out = out;
		this.targetClass = constantPool.addClass(constantPool.addUtf8(className));
	}

	@Override
	public void begin() {
		this.code.addAll(List.of( //
				Opcode.SIPUSH, 0x04, 0x00, /* 1024 */ //
				Opcode.NEWARRAY, ArrayType.T_INT, Opcode.ASTORE_1, // memory
				Opcode.ICONST_0, Opcode.ISTORE_2 // pointer
		));

		// Clear any previous data when reusing the generator
		this.branchTargets.clear();
	}

	@Override
	public void end() {
		this.code.add(Opcode.RETURN);
		generateStackMapTableAndWriteClass();
	}

	private void generateStackMapTableAndWriteClass() {
		final ClassConstant javaLangObjectClass = constantPool.addClass(constantPool.addUtf8("java/lang/Object"));
		final Utf8Constant mainUt8 = constantPool.addUtf8("main");
		final Utf8Constant javaLangStringArrayType = constantPool.addUtf8("([Ljava/lang/String;)V");
		final Utf8Constant codeUtf8 = constantPool.addUtf8("Code");
		final Utf8Constant stackMapTableUtf8 = constantPool.addUtf8("StackMapTable");

		final ClassConstant intArrayClass = constantPool.addClass(constantPool.addUtf8("[I"));
		final ByteArrayOutputStream stackMapTableData = generateMinimalStackMapTable(intArrayClass);

		new ByteCodeWriter(this.out) //
			.write(0xca, 0xfe, 0xba, 0xbe) // cafebabe
			.writeVersion(0, 50) // Java 6
			.writeConstantPool(constantPool) //
			.writeClass(AccessFlag.ACC_PUBLIC, targetClass, javaLangObjectClass) //
			.writeInterfaces(interfaces -> {
			})
			.writeFields(fields -> {
			})
			.writeMethods(methods -> methods.add(AccessFlag.ACC_PUBLIC + AccessFlag.ACC_STATIC, mainUt8,
					javaLangStringArrayType,
					method -> method.writeAttributes(attributes -> attributes.add(codeUtf8, attribute -> {
						attribute.writeU2(4) // max_stack
							.writeU2(3) // max_locals
							.writeCode((Object[]) code.toArray(code.toArray(new Integer[0])))
							.writeU2(0) // exception_table_length
							.writeAttributes(attrs -> attrs.add(stackMapTableUtf8, attr -> {
								attr.write(stackMapTableData.toByteArray());
							}));
					})))) //
			.writeAttributes(attributesDef -> {
			});
	}

	private ByteArrayOutputStream generateMinimalStackMapTable(ClassConstant intArrayClass) {
		ByteArrayOutputStream stackMapTableData = new ByteArrayOutputStream();
		ByteCodeWriter stackMapWriter = new ByteCodeWriter(stackMapTableData);

		// Simple StackMapTable: initial frame + branch targets
		var sortedTargets = branchTargets.stream()
			.distinct()
			.filter(target -> target > 8) // Only targets after initial setup
			.sorted()
			.limit(99) // Limit to prevent excessive frames
			.toList();

		// Write the number of frames (initial frame + branch targets)
		stackMapWriter.writeU2(sortedTargets.size() + 1);

		// First frame: APPEND frame to establish local variables
		stackMapWriter.write(253); // frame_type = append_frame (2 locals)
		stackMapWriter.writeU2(6); // offset_delta = 7-1 (at istore_2 instruction)
		stackMapWriter.write(7); // ITEM_Object for int[]
		stackMapWriter.writeU2(intArrayClass.index());
		stackMapWriter.write(1); // ITEM_Integer for pointer

		// Add frames for all branch targets
		int previousOffset = 6;
		for (Integer target : sortedTargets) {
			int offsetDelta = target - previousOffset - 1;
			if (offsetDelta >= 0 && offsetDelta <= 65535) {
				stackMapWriter.write(251); // frame_type = same_frame_extended
				stackMapWriter.writeU2(offsetDelta);
				previousOffset = target;
			}
		}

		return stackMapTableData;
	}

	@Override
	public void generateLoopStatement(LoopStatement statement) {
		// Use GOTO_W from the start to handle large offsets
		final int loopStart = this.code.size();

		this.code.addAll(List.of( //
				Opcode.ALOAD_1, // memory
				Opcode.ILOAD_2, // pointer
				Opcode.IALOAD, // memory[pointer]
				Opcode.IFNE, 0, 8 // jump 8 bytes to skip the GOTO_W if non-zero
		));

		// Add GOTO_W to exit the loop when value is zero
		this.code.add(Opcode.GOTO_W);
		final int gotoWExitPosition = this.code.size() - 1;
		this.writeBytes(toU4(0)); // Placeholder for exit offset

		// Generate loop body
		statement.statements().forEach(s -> s.generate(this));

		// Add GOTO_W back to loop start
		this.code.add(Opcode.GOTO_W);
		final int gotoWBackPosition = this.code.size() - 1;
		final int gotoWBackOffset = loopStart - gotoWBackPosition;
		this.writeBytes(toU4(gotoWBackOffset));

		// Fix the GOTO_W exit offset
		final int endPosition = this.code.size();
		final int gotoWExitOffset = endPosition - gotoWExitPosition;
		final byte[] exitBytes = toU4(gotoWExitOffset);
		this.code.set(gotoWExitPosition + 1, (int) exitBytes[0]);
		this.code.set(gotoWExitPosition + 2, (int) exitBytes[1]);
		this.code.set(gotoWExitPosition + 3, (int) exitBytes[2]);
		this.code.set(gotoWExitPosition + 4, (int) exitBytes[3]);

		// Record branch target for StackMapTable
		this.branchTargets.add(endPosition);
	}

	@Override
	public void generateOutputStatement(OutputStatement statement) {
		this.code.add(Opcode.GETSTATIC);
		this.writeBytes(systemOutFieldRef.indexAsU2() /* System.out */);
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
		this.code.addAll(List.of( //
				Opcode.ALOAD_1, // memory
				Opcode.ILOAD_2, // pointer,
				Opcode.GETSTATIC));
		this.writeBytes(systemInFieldRef.indexAsU2() /* System.in */);
		this.code.add(Opcode.INVOKEVIRTUAL);
		this.writeBytes(readMethodRef.indexAsU2() /* read */);
		this.code.add(Opcode.IASTORE);
	}

	@Override
	public void generateIncrementValueExpression(IncrementValueExpression expression) {
		this.code.addAll(List.of( //
				Opcode.ALOAD_1, // memory
				Opcode.ILOAD_2, // pointer
				Opcode.DUP2, //
				Opcode.IALOAD // memory[pointer]
		));
		this.code.addAll(intToCode(Math.abs(expression.value())));
		this.code.addAll(List.of( //
				expression.value() > 0 ? Opcode.IADD /* + */ : Opcode.ISUB /* - */, //
				Opcode.IASTORE //
		));
	}

	static List<Integer> intToCode(int i) {
		if (i < 6) {
			return List.of(Opcode.ICONST_0 + i);
		}
		else if (i <= Byte.MAX_VALUE) {
			return List.of(Opcode.BIPUSH, i);
		}
		else {
			// TODO if greater than 2 bytes
			return List.of(Opcode.SIPUSH, i);
		}
	}

	@Override
	public void generateIncrementPointerExpression(IncrementPointerExpression expression) {
		this.code.addAll(List.of( //
				Opcode.IINC, //
				0x02, // pointer
				expression.value()));
	}

	@Override
	public void generateSetValueToZeroExpression(ResetValueExpression expression) {
		this.code.addAll(List.of( //
				Opcode.ALOAD_1, // memory
				Opcode.ILOAD_2, // pointer
				Opcode.ICONST_0, // value 0
				Opcode.IASTORE)); // memory[pointer] = 0
	}

	private void writeBytes(byte[] bytes) {
		for (byte b : bytes) {
			this.code.add((int) b);
		}
	}

	private static byte[] toU4(int i) {
		return ByteBuffer.allocate(4).putInt(i).array();
	}

}
