# bfc

Braininf*ck Compiler/Interpreter

![bfc](https://github.com/making/bfc/assets/106908/a1c49eed-b6e8-4c15-8115-6d4812b69f49)

## How to build bfc

### Native Binary (GraalVM required)

```
./mvnw package -Pnative
install target/bfc /usr/local/bin/
```

```
$ bfc -h
Braininf*ck Compiler/Interpreter

bfc <input file> [options]

---
Options:
-o:             output file name of the compilation (supported extensions: *.js, *.java, *.class, *.wat).
                without this option bfc works as an interpreter.
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

bfc <input file> [options]

---
Options:
-o:             output file name of the compilation (supported extensions: *.js, *.java, *.class, *.wat).
                without this option bfc works as an interpreter.
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
$ java Hello
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