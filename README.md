# bfc

Braininf*ck Compiler/Interpreter

![bfc](https://github.com/making/bfc/assets/106908/a1c49eed-b6e8-4c15-8115-6d4812b69f49)

## Install

### Install via Homebrew (Mac / Linux)

You can install native binary for Mac (only for Intel) or Linux via [Homebrew](https://brew.sh/).

```
brew install making/tap/bfc
```

## How to build bfc

### Native Binary (GraalVM required)

```
./mvnw package -Pnative
install target/bfc /usr/local/bin/
```

```
$ bfc -h
Braininf*ck Compiler/Interpreter

bfc <input file or '-' as stdin> [options]

---
Options:
-o:             output file name of the compilation (supported extensions: *.js, *.java, *.class, *.wat, *.wasm).
                without this option bfc works as an interpreter.
--optimize:     Enable optimizers
-v, --version:  print version
-h, --help:     print this help
```

### Executable jar

```
./mvnw package
```

```
$ java -jar target/bfc-0.1.0-SNAPSHOT.jar -h
Braininf*ck Compiler/Interpreter

bfc <input file or '-' as stdin> [options]

---
Options:
-o:             output file name of the compilation (supported extensions: *.js, *.java, *.class, *.wat, *.wasm).
                without this option bfc works as an interpreter.
--optimize:     Enable optimizers
-v, --version:  print version
-h, --help:     print this help
```


## How to use bfc

### Interpreter

```
$ bfc examples/hello.bf
Hello World
```

### Compiler to JVM bytecode

```
$ bfc examples/hello.bf -o Hello.class
$ file Hello.class                               
Hello.class: compiled Java class data, version 50.0 (Java 1.6)
$ java Hello
Hello World
```

### Compiler to WASM

```
$ bfc examples/hello.bf -o hello.wasm
$ file hello.wasm
hello.wasm: WebAssembly (wasm) binary module version 0x1 (MVP)
$ wasmtime hello.wasm
Hello World
```

### Compiler to WAT

```
$ bfc examples/hello.bf -o hello.wat
$ wasmtime hello.wat
Hello World
```

### Compiler to Java

```
$ bfc examples/hello.bf -o Hello.java
$ java Hello.java
Hello World
```

### Compiler to JavaScript

```
$ bfc examples/hello.bf -o hello.js
$ node hello.js
Hello World
```