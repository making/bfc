package am.ik.jvm;

import java.util.function.Consumer;

import am.ik.jvm.ConstantPool.Utf8Constant;

public final class MethodsDef extends CountingDef<MethodsDef> {

	public MethodsDef add(int accessFlag, Utf8Constant name, Utf8Constant descriptor,
			Consumer<ByteCodeWriter> consumer) {
		return this.add(byteCode -> consumer.accept(byteCode.writeU2(accessFlag).writeU2(name).writeU2(descriptor)));
	}

}
