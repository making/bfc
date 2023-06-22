package am.ik.wasm;

public class FunctionDef extends CountingDef<FunctionDef> {

	public FunctionDef addFunction(int signatureIndex) {
		return this.add(function -> function.write(signatureIndex));
	}

}
