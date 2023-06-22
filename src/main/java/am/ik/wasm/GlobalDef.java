package am.ik.wasm;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

public class GlobalDef extends CountingDef<GlobalDef> {

	public GlobalDef addGlobal(Type type, Mutability mutability, Consumer<WasmWriter> consumer) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		consumer.accept(new WasmWriter(stream));
		return this.add(global -> global.write(type, mutability, stream.toByteArray(), Instruction.END));
	}

}
