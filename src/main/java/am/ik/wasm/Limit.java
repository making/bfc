package am.ik.wasm;

public enum Limit implements Codable {

	MIN(0), MINMAX(1);

	private final int code;

	Limit(int code) {
		this.code = code;
	}

	@Override
	public int code() {
		return this.code;
	}

}
