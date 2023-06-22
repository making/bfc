package am.ik.wasm;

/**
 * <a href=
 * "https://webassembly.github.io/spec/core/binary/modules.html#sections">Section</a>
 */
public enum Section implements Codable {

	CUSTOM(0), //
	TYPE(1), //
	IMPORT(2), //
	FUNCTION(3), //
	TABLE(4), //
	MEMORY(5), //
	GLOBAL(6), //
	EXPORT(7), //
	START(8), //
	ELEMENT(9), //
	CODE(10), //
	DATA(11), //
	DATA_COUNT(12) //
	;

	private final int code;

	Section(int code) {
		this.code = code;
	}

	@Override
	public int code() {
		return code;
	}

}
