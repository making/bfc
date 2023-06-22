package am.ik.wasm;

public class CodeDef extends CountingDef<CodeDef> {

	private int declCount = 0;

	public CodeDef addFunction(byte[] body) {
		return this.add(function -> function.writeSignedLeb128(body.length).write(body));
	}

}
