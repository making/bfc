package am.ik.wasm;

public class TypeDef extends CountingDef<TypeDef> {

	public TypeDef addFunc(Type[] params, Type[] results) {
		return this.add(type -> type.write(Type.FUNC, params.length, params, results.length, results));
	}

}
