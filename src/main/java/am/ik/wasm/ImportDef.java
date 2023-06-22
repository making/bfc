package am.ik.wasm;

public class ImportDef extends CountingDef<ImportDef> {

	public ImportDef addImport(String moduleName, String fieldName, ExternalKind externalKind, int signatureIndex) {
		return this.add(imprt -> imprt.write(moduleName.length(), moduleName, //
				fieldName.length(), fieldName, //
				externalKind, signatureIndex));
	}

}
