package am.ik.wasm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class WasmWriter {

	private final OutputStream out;

	public WasmWriter(OutputStream out) {
		this.out = out;
	}

	public WasmWriter writeLittleEndian4(int i) {
		return this.write((Object) ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array());
	}

	public WasmWriter writeSignedLeb12(int i) {
		// https://en.wikipedia.org/wiki/LEB128#Encode_signed_32-bit_integer
		int value = i;
		value |= 0;
		final List<Byte> result = new ArrayList<>();
		while (true) {
			final int b = value & 0x7f;
			value >>= 7;
			if ((value == 0 && (b & 0x40) == 0) || (value == -1 && (b & 0x40) != 0)) {
				result.add((byte) b);
				break;
			}
			result.add((byte) (b | 0x80));
		}
		return this.write(result);
	}

	public WasmWriter writeLittleEndian2(int i) {
		return this.write((Object) ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).putShort((short) i).array());
	}

	public WasmWriter writeLittleEndian1(int i) {
		return this.write((Object) ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put((byte) i).array());
	}

	public WasmWriter write(Object... objects) {
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
				else if (o instanceof Codable) {
					out.write(((Codable) o).code());
				}
				else if (o instanceof Object[]) {
					this.write((Object[]) o);
				}
				else if (o instanceof List<?>) {
					((List<?>) o).forEach(this::write);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WasmWriter writeSection(Section section, Consumer<CountingDef> consumer) {
		return this.writeSection(section, consumer, CountingDef::new);
	}

	public <T extends CountingDef<T>> WasmWriter writeSection(Section section, Consumer<T> consumer,
			Supplier<T> defSupplier) {
		final T def = defSupplier.get();
		consumer.accept(def);
		final byte[] bytes = def.toByteArray();
		return this.write(section).writeSignedLeb12(bytes.length).write(bytes);
	}

	public WasmWriter writeTypeSection(Consumer<TypeDef> consumer) {
		return this.writeSection(Section.TYPE, consumer, TypeDef::new);
	}

	public WasmWriter writeImportSection(Consumer<ImportDef> consumer) {
		return this.writeSection(Section.IMPORT, consumer, ImportDef::new);
	}

	public WasmWriter writeFunction(Consumer<FunctionDef> consumer) {
		return this.writeSection(Section.FUNCTION, consumer, FunctionDef::new);
	}

	public WasmWriter writeMemory(Consumer<MemoryDef> consumer) {
		return this.writeSection(Section.MEMORY, consumer, MemoryDef::new);
	}

	public WasmWriter writeGlobal(Consumer<GlobalDef> consumer) {
		return this.writeSection(Section.GLOBAL, consumer, GlobalDef::new);
	}

	public WasmWriter writeExport(Consumer<ExportDef> consumer) {
		return this.writeSection(Section.EXPORT, consumer, ExportDef::new);
	}

	public WasmWriter writeCode(Consumer<CodeDef> consumer) {
		return this.writeSection(Section.CODE, consumer, CodeDef::new);
	}

}
