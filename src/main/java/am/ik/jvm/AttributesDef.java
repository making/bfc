package am.ik.jvm;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import am.ik.jvm.ConstantPool.Utf8Constant;

public final class AttributesDef extends CountingDef<AttributesDef> {

	public AttributesDef add(Utf8Constant attributeName, Consumer<ByteCodeWriter> consumer) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final ByteCodeWriter out = new ByteCodeWriter(stream);
		consumer.accept(out);
		return this.add(attribute -> attribute.writeU2(attributeName)
			.writeU4(stream.size()) // attribute_length
			.write(stream.toByteArray()) // attribute
		);
	}

}
