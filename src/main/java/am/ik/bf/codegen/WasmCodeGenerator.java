package am.ik.bf.codegen;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.InputStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.OutputStatement;
import am.ik.wasm.ExternalKind;
import am.ik.wasm.Instruction;
import am.ik.wasm.Mutability;
import am.ik.wasm.Type;
import am.ik.wasm.WasmWriter;

public class WasmCodeGenerator implements CodeGenerator {

	private final OutputStream out;

	private final ByteArrayOutputStream code = new ByteArrayOutputStream();

	private final WasmWriter codeWriter = new WasmWriter(this.code);

	public WasmCodeGenerator(OutputStream out) {
		this.out = out;
	}

	@Override
	public void begin() {
		this.code.write(0); // local decl count
	}

	@Override
	public void end() {
		this.codeWriter.write(Instruction.END);
		new WasmWriter(out) //
			.write("\0asm") // WASM_BINARY_MAGIC
			.writeLittleEndian4(1) // WASM_BINARY_VERSION
			// section "Type" (1)
			.writeTypeSection(types -> types
				// func type 0
				.addFunc(new Type[] { Type.I32, Type.I32, Type.I32, Type.I32 }, new Type[] { Type.I32 })
				// func type 1
				.addFunc(new Type[] {}, new Type[] {}))
			// section "Import" (2)
			.writeImportSection(imports -> imports //
				// func 0
				.addImport("wasi_snapshot_preview1", "fd_write", ExternalKind.FUNCTION, 0)
				// func 1
				.addImport("wasi_snapshot_preview1", "fd_read", ExternalKind.FUNCTION, 0))
			// section "Function" (3)
			.writeFunction(functions -> functions //
				// func 2
				.addFunction(1))
			// section "Memory" (5)
			.writeMemory(memories -> memories.addMemory(1))
			// section "Global" (6)
			.writeGlobal(globals -> globals.addGlobal(Type.I32, Mutability.VAR, instructions -> instructions //
				.write(Instruction.I32_CONST)
				.writeSignedLeb128(128)))
			// section "Export" (7)
			.writeExport(exports -> exports //
				.addExport("memory", ExternalKind.MEMORY, 0)
				.addExport("_start", ExternalKind.FUNCTION, 2))
			// section "Code" (10)
			.writeCode(functions -> functions //
				.addFunction(this.code.toByteArray()));
	}

	@Override
	public void generateLoopStatement(LoopStatement statement) {
		this.codeWriter.write(Instruction.LOOP, 0x40, // loop void
				Instruction.BLOCK, 0x40, // block void
				Instruction.GET_GLOBAL, 0, // global.get 0
				Instruction.I32_LOAD8_U, 0, 0, // i32.load8_u align=0 offset=0
				Instruction.I32_EQZ, // i32.eqz
				Instruction.BR_IF, 0// br_if 0
		);
		statement.statements().forEach(s -> s.generate(this));
		this.codeWriter.write(Instruction.BR, 1, // br 1
				Instruction.END, // end
				Instruction.END // end
		);
	}

	@Override
	public void generateOutputStatement(OutputStatement statement) {
		this.codeWriter.write( //
				// iov
				Instruction.I32_CONST, 0, // i32.const 0
				Instruction.GET_GLOBAL, 0, // global.get 0
				Instruction.I32_STORE, 2, 0, // i32.store align=2 offset=0
				Instruction.I32_CONST, 4, // i32.const 4
				Instruction.I32_CONST, 1, // i32.const 1 ;; 1 byte
				Instruction.I32_STORE, 2, 0, // i32.store align=2 offset=0
				// print
				Instruction.I32_CONST, 1, // i32.const 1 ;; stdout
				Instruction.I32_CONST, 0, // i32.const 0 ;; iov index
				Instruction.I32_CONST, 1, // i32.const 1 ;; iov len
				Instruction.I32_CONST, 20, // i32.const 20
				Instruction.CALL, 0, // call 0
				Instruction.DROP // drop
		);
	}

	@Override
	public void generateInputStatement(InputStatement statement) {
		this.codeWriter.write( //
				// iov
				Instruction.I32_CONST, 0, // i32.const 0
				Instruction.GET_GLOBAL, 0, // global.get 0
				Instruction.I32_STORE, 2, 0, // i32.store align=2 offset=0
				Instruction.I32_CONST, 4, // i32.const 4
				Instruction.I32_CONST, 1, // i32.const 1 ;; 1 byte
				Instruction.I32_STORE, 2, 0, // i32.store align=2 offset=0
				// read
				Instruction.I32_CONST, 0, // i32.const 0 ;; stdin
				Instruction.I32_CONST, 0, // i32.const 0 ;; iov index
				Instruction.I32_CONST, 1, // i32.const 1 ;; iov len
				Instruction.I32_CONST, 20, // i32.const 20
				Instruction.CALL, 1, // call 1
				Instruction.DROP // drop
		);
	}

	@Override
	public void generateIncrementValueExpression(IncrementValueExpression expression) {
		this.codeWriter.write( //
				Instruction.GET_GLOBAL, 0, // global.get 0
				Instruction.GET_GLOBAL, 0, // global.get 0
				Instruction.I32_LOAD8_U, 0, 0, // i32.load_u align=0 offset=0
				Instruction.I32_CONST, 1, // i32.const 1
				(expression.value() > 0 ? Instruction.I32_ADD : Instruction.I32_SUB), // i32.add-sub
				Instruction.I32_STORE8, 0, 0 // i32.store8 align=0 offset=0
		);
	}

	@Override
	public void generateIncrementPointerExpression(IncrementPointerExpression expression) {
		this.codeWriter.write( //
				Instruction.GET_GLOBAL, 0, // global.get 0
				Instruction.I32_CONST, 1, // i32.const 1
				(expression.value() > 0 ? Instruction.I32_ADD : Instruction.I32_SUB), // i32.add-sub
				Instruction.SET_GLOBAL, 0 // global.set 0
		);
	}

}
