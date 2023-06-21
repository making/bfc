package am.ik.bf.codegen;

import java.io.OutputStream;

import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.InputStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.OutputStatement;

public class WatCodeGenerator extends PrintStreamCodeGenerator {

	private final int loopDepth;

	public WatCodeGenerator(OutputStream out) {
		this(out, 0);
	}

	public WatCodeGenerator(OutputStream out, int loopDepth) {
		super(out);
		this.loopDepth = loopDepth;
	}

	public WatCodeGenerator inLoop() {
		return new WatCodeGenerator(this.out, this.loopDepth + 1);
	}

	@Override
	public void begin() {
		this.out.print("""
				(module
				    (import "wasi_snapshot_preview1" "fd_write" (func $fd_write (param i32 i32 i32 i32) (result i32)))
				    (import "wasi_snapshot_preview1" "fd_read"  (func $fd_read  (param i32 i32 i32 i32) (result i32)))
				    (memory (export "memory") 1)
				    (global $pointer (mut i32) (i32.const 128))
				    (func (export "_start")
				""");
	}

	@Override
	public void end() {
		this.out.print("""
				    )
				)""");

	}

	@Override
	public void generateLoopStatement(LoopStatement statement) {
		this.out.printf("""
				        loop ;; @%d
				        block ;; @%d
				        global.get $pointer
				        i32.load8_u
				        i32.eqz
				        br_if 0 ;; @%d
				""", this.loopDepth * 2 + 1, this.loopDepth * 2 + 2, this.loopDepth * 2 + 2);
		statement.statements().forEach(s -> s.generate(this.inLoop()));
		this.out.printf("""
				        br 1 ;; @%d
				        end ;; @%d
				        end ;; @%d
				""", this.loopDepth * 2 + 1, this.loopDepth * 2 + 2, this.loopDepth * 2 + 1);
	}

	@Override
	public void generateOutputStatement(OutputStatement statement) {
		this.out.print("""
				        ;; .
				        ;; iov
				        i32.const 0
				        global.get $pointer
				        i32.store
				        i32.const 4
				        i32.const 1 ;; 1 byte
				        i32.store
				        ;; print
				        i32.const 1 ;; stdout
				        i32.const 0 ;; iov index
				        i32.const 1 ;; iov len
				        i32.const 20
				        call $fd_write
				        drop
				""");
	}

	@Override
	public void generateInputStatement(InputStatement statement) {
		this.out.print("""
				        ;; ,
				        ;; iov
				        i32.const 0
				        global.get $pointer
				        i32.store
				        i32.const 4
				        i32.const 1 ;; 1 byte
				        i32.store
				        ;; read
				        i32.const 0 ;; stdin
				        i32.const 0 ;; iov index
				        i32.const 1 ;; iov len
				        i32.const 20
				        call $fd_read
				        drop
				""");
	}

	@Override
	public void generateIncrementValueExpression(IncrementValueExpression expression) {
		this.out.printf("""
				        ;; %s
				        global.get $pointer
				        global.get $pointer
				        i32.load8_u
				        i32.const 1
				        i32.%s
				        i32.store8
				""", expression.value() > 0 ? "+" : "-", expression.value() > 0 ? "add" : "sub");
	}

	@Override
	public void generateIncrementPointerExpression(IncrementPointerExpression expression) {
		this.out.printf("""
				        ;; %s
				        global.get $pointer
				        i32.const 1
				        i32.%s
				        global.set $pointer
				""", expression.value() > 0 ? ">" : "<", expression.value() > 0 ? "add" : "sub");
	}

}
