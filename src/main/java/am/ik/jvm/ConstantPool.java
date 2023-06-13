package am.ik.jvm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public final class ConstantPool {

	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	private int size = 0;

	public Constant add(ConstantType constantType, Consumer<ByteCodeWriter> constantDef) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final ByteCodeWriter out = new ByteCodeWriter(stream);
		out.write(constantType.value());
		constantDef.accept(out);
		try {
			this.out.write(stream.toByteArray());
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return new Constant(++this.size, constantType, stream.toByteArray());
	}

	public Utf8Constant addUtf8(String s) {
		return new Utf8Constant(this.add(ConstantType.UTF8, o -> o.writeUtf8Info(s)));
	}

	public ClassConstant addClass(Utf8Constant classUtf8) {
		return new ClassConstant(this.add(ConstantType.CLASS, o -> o.writeU2(classUtf8)));
	}

	public NameAndTypeConstant addNameAndType(Utf8Constant nameUtf8, Utf8Constant typeUtf8) {
		return new NameAndTypeConstant(
				this.add(ConstantType.NAME_AND_TYPE, o -> o.writeU2(nameUtf8).writeU2(typeUtf8)));
	}

	public FieldrefConstant addFieldref(ClassConstant clazz, NameAndTypeConstant nameAndType) {
		return new FieldrefConstant(this.add(ConstantType.FIELDREF, o -> o.writeU2(clazz).writeU2(nameAndType)));
	}

	public MethodrefConstant addMethodref(ClassConstant clazz, NameAndTypeConstant nameAndType) {
		return new MethodrefConstant(this.add(ConstantType.METHODREF, o -> o.writeU2(clazz).writeU2(nameAndType)));
	}

	public StringConstant addString(Utf8Constant utf8) {
		return new StringConstant(this.add(ConstantType.STRING, o -> o.writeU2(utf8)));
	}

	public StringConstant addString(String s) {
		return this.addString(this.addUtf8(s));
	}

	public int size() {
		return this.size;
	}

	public byte[] toByteArray() {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final ByteCodeWriter out = new ByteCodeWriter(stream);
		out.writeU2(this.size + 1);
		out.write(this.out.toByteArray());
		return stream.toByteArray();
	}

	public static class Constant {

		private final int index;

		private final ConstantType type;

		private final byte[] bytes;

		public Constant(int index, ConstantType type, byte[] bytes) {
			this.index = index;
			this.type = type;
			this.bytes = bytes;
		}

		public int index() {
			return index;
		}

		public byte[] indexAsU2() {
			return ByteBuffer.allocate(2).putShort((short) index).array();
		}

		public ConstantType type() {
			return type;
		}

		public byte[] bytes() {
			return bytes;
		}

	}

	public static class Utf8Constant extends Constant {

		public Utf8Constant(Constant constant) {
			super(constant.index, constant.type(), constant.bytes());
		}

	}

	public static class ClassConstant extends Constant {

		public ClassConstant(Constant constant) {
			super(constant.index, constant.type(), constant.bytes());
		}

	}

	public static class NameAndTypeConstant extends Constant {

		public NameAndTypeConstant(Constant constant) {
			super(constant.index, constant.type(), constant.bytes());
		}

	}

	public static class FieldrefConstant extends Constant {

		public FieldrefConstant(Constant constant) {
			super(constant.index, constant.type(), constant.bytes());
		}

	}

	public static class MethodrefConstant extends Constant {

		public MethodrefConstant(Constant constant) {
			super(constant.index, constant.type(), constant.bytes());
		}

	}

	public static class StringConstant extends Constant {

		public StringConstant(Constant constant) {
			super(constant.index, constant.type(), constant.bytes());
		}

	}

}
