package am.ik.wasm;

public class ExportDef extends CountingDef<ExportDef> {

	public ExportDef addExport(String exportName, ExternalKind externalKind, int signatureIndex) {
		return this.add(export -> export.write(exportName.length(), exportName, //
				externalKind, signatureIndex));
	}

}
