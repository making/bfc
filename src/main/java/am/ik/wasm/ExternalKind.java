package am.ik.wasm;

public enum ExternalKind implements Codable {

	FUNCTION(0), //
	TABLE(1), //
	MEMORY(2), //
	GLOBAL(3) //
	;

	private final int code;

	ExternalKind(int code) {
		this.code = code;
	}

	@Override
	public int code() {
		return code;
	}

}
