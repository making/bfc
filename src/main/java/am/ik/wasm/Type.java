package am.ik.wasm;

/**
 * <a href="https://webassembly.github.io/spec/core/binary/types.html#types">Types</a>
 */
public enum Type implements Codable {

	I32(0x7f), //
	I64(0x7E), //
	F32(0x7D), //
	F64(0x7C), //
	V128(0x7B), //
	FUNCREF(0x70), //
	EXTERNREF(0x6F), //
	FUNC(0x60) //
	;

	private final int code;

	Type(int code) {
		this.code = code;
	}

	@Override
	public int code() {
		return code;
	}

}
