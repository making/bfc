package am.ik.jvm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

public class CountingDef<T extends CountingDef<?>> {

	protected final ByteArrayOutputStream out = new ByteArrayOutputStream();

	protected int count = 0;

	@SuppressWarnings("unchecked")
	public T add(Consumer<ByteCodeWriter> consumer) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final ByteCodeWriter out = new ByteCodeWriter(stream);
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
		final ByteCodeWriter out = new ByteCodeWriter(stream);
		out.writeU2(this.count);
		out.write(this.out.toByteArray());
		return stream.toByteArray();
	}

}
