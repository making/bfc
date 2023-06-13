package am.ik.jvm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import am.ik.jvm.ConstantPool.ClassConstant;
import am.ik.jvm.ConstantPool.Constant;

public class ByteCodeWriter {

	private final OutputStream out;

	public ByteCodeWriter(OutputStream out) {
		this.out = out;
	}

	public ByteCodeWriter writeU4(int n) {
		try {
			this.out.write(ByteBuffer.allocate(4).putInt(n).array());
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return this;
	}

	public ByteCodeWriter writeU2(short n) {
		try {
			this.out.write(ByteBuffer.allocate(2).putShort(n).array());
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return this;
	}

	public ByteCodeWriter writeU2(int n) {
		return this.writeU2((short) n);
	}

	public ByteCodeWriter writeU2(Constant c) {
		return this.writeU2(c.index());
	}

	public ByteCodeWriter writeUtf8Info(String s) {
		try {
			final int len = s.length();
			this.writeU2(len);
			out.write(s.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return this;
	}

	public ByteCodeWriter writeCode(Object... code) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new ByteCodeWriter(stream).write(code);
		final byte[] bytes = stream.toByteArray();
		return this.writeU4(bytes.length).write(bytes);
	}

	public ByteCodeWriter write(byte[] bytes) {
		try {
			out.write(bytes);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return this;
	}

	public ByteCodeWriter write(Object... objects) {
		try {
			for (Object o : objects) {
				if (o instanceof Integer) {
					out.write((Integer) o);
				}
				else if (o instanceof Byte) {
					out.write((Byte) o);
				}
				else if (o instanceof byte[]) {
					out.write((byte[]) o);
				}
				else if (o instanceof String) {
					out.write(((String) o).getBytes(StandardCharsets.UTF_8));
				}
				else {
					throw new IllegalStateException(o.getClass() + " is not supported");
				}
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return this;
	}

	public ByteCodeWriter writeVersion(int minorVersion, int majorVersion) {
		return this.writeU2(minorVersion).writeU2(majorVersion);
	}

	public ByteCodeWriter writeConstantPool(ConstantPool constantPool) {
		return this.write(constantPool.toByteArray());
	}

	public ByteCodeWriter writeConstantPool(Consumer<ConstantPool> consumer) {
		final ConstantPool constantPool = new ConstantPool();
		consumer.accept(constantPool);
		return this.writeConstantPool(constantPool);
	}

	public ByteCodeWriter writeClass(int accessFlag, ClassConstant thisClass, ClassConstant superClass) {
		return this.writeU2(accessFlag).writeU2(thisClass).writeU2(superClass);
	}

	public ByteCodeWriter writeInterfaces(Consumer<CountingDef<?>> consumer) {
		final CountingDef<?> def = new CountingDef<>();
		return this.write(def.toByteArray());
	}

	public ByteCodeWriter writeFields(Consumer<CountingDef<?>> consumer) {
		final CountingDef<?> def = new CountingDef<>();
		return this.write(def.toByteArray());
	}

	public ByteCodeWriter writeMethods(MethodsDef methodsDef) {
		return this.write(methodsDef.toByteArray());
	}

	public ByteCodeWriter writeMethods(Consumer<MethodsDef> consumer) {
		final MethodsDef methodsDef = new MethodsDef();
		consumer.accept(methodsDef);
		return this.writeMethods(methodsDef);
	}

	public ByteCodeWriter writeAttributes(AttributesDef attributesDef) {
		return this.write(attributesDef.toByteArray());
	}

	public ByteCodeWriter writeAttributes(Consumer<AttributesDef> consumer) {
		final AttributesDef attributesDef = new AttributesDef();
		consumer.accept(attributesDef);
		return this.writeAttributes(attributesDef);
	}

}
