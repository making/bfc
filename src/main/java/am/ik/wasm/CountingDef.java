package am.ik.wasm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

public class CountingDef<T extends CountingDef<?>> {

	protected final ByteArrayOutputStream out = new ByteArrayOutputStream();

	protected int count = 0;

	@SuppressWarnings("unchecked")
	public T add(Consumer<WasmWriter> consumer) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final WasmWriter out = new WasmWriter(stream);
		this.count++;
		consumer.accept(out);
		try {
			this.out.write(stream.toByteArray());
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return (T) this;
	}

	protected final byte[] toByteArray() {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final WasmWriter out = new WasmWriter(stream);
		out.write(this.count);
		out.write((Object) this.out.toByteArray());
		return stream.toByteArray();
	}

}
