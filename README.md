# Java Compiler

A modular Java-based compiler project focused on parsing Java-like tokens, performing semantic analysis, and generating x86-64 assembly.

The project is being developed with separate type-specific modules that can eventually be integrated into a common compiler architecture.

## Project Structure

```text
javacompiler/
│
├── README.md
├── .gitignore
│
└── modules/
    └── boolean/
        ├── docs/
        ├── src/
        ├── test/
        ├── run-tests.sh
        └── run-tests.ps1
```

## Modules

| Module | Contributor(s) | Status |
|---|---|---|
| Boolean | Ankit Bind | Implemented |
| Integer | Krishna, Jatin, Janesh, Riya | Planned |
| String | Ayush | Planned |

## Boolean Module

The Boolean module is implemented in Java and supports:

- Boolean declarations and assignments
- Logical operators: `&&`, `||`, `!`
- Bitwise operators: `&`, `|`, `^`
- Equality and inequality operators
- Expression parsing and operator precedence
- Abstract Syntax Tree (AST)
- Semantic validation
- x86-64 NASM assembly generation
- Short-circuit evaluation
- Expression depth protection
- Standalone and library-style compilation

### Technologies

- Java
- NASM
- x86-64 Assembly
- GCC
- Linux / WSL

## Running Tests

Open a Linux or WSL terminal and navigate to the Boolean module:

```bash
cd modules/boolean
```

Run:

```bash
bash run-tests.sh
```

The test suite includes lexer, parser, semantic analysis, assembly execution, and library integration tests.

### Requirements

- JDK 21 or compatible Java version
- NASM
- GCC
- GNU binutils (`ld`, `readelf`)

## Project Goal

The goal is to develop a modular compiler architecture in Java that can parse different data types and expressions and generate general x86-64 assembly output.

Each type-specific module will be developed independently and integrated into a common compiler architecture.

## Contributors

- Ankit Bind — Boolean Module
- Krishna — Integer Module
- Jatin — Integer Module
- Janesh — Integer Module
- Riya — Integer Module
- Ayush — String Module

---

**Project:** Java Compiler  
**Repository:** [AnkitBind21/javacompiler](https://github.com/AnkitBind21/javacompiler)
