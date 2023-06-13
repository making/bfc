package am.ik.jvm;

public enum ConstantType {

	CLASS(7),

	FIELDREF(9),

	METHODREF(10),

	INTERFACE_METHODREF(11),

	STRING(8),

	INTEGER(3),

	FLOAT(4),

	LONG(5),

	DOUBLE(6),

	NAME_AND_TYPE(12),

	UTF8(1),

	METHOD_HANDLE(15),

	METHOD_TYPE(16),

	INVOKE_DYNAMIC(18);

	private final int value;

	ConstantType(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

}
