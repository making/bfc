package am.ik.wasm;

public enum Mutability implements Codable {

	CONST(0), VAR(1);

	private final int code;

	Mutability(int code) {
		this.code = code;
	}

	@Override
	public int code() {
		return this.code;
	}

}
