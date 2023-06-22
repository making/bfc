package am.ik.wasm;

public class MemoryDef extends CountingDef<MemoryDef> {

	public MemoryDef addMemory(int initial) {
		return this.add(memory -> memory.write(Limit.MIN, initial));
	}

	public MemoryDef addMemory(int initial, int maximum) {
		return this.add(memory -> memory.write(Limit.MINMAX, initial, maximum));
	}

}
