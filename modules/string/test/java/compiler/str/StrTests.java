package compiler.str;

import java.util.ArrayList;

/**
 * Tests for the String module. One check() helper prints PASS or FAIL,
 * and one method tests each phase: lexer, parser, errors, checker, codegen, abstract classes.
 * We only check the assembly text, we do not run it with nasm.
 *
 * Run (from modules/strings):
 *   javac -d out src/java/compiler/str/*.java test/java/compiler/str/StrTests.java
 *   java -cp out compiler.str.StrTests
 */
class StrTests {
    private static int passed = 0;
    private static int failed = 0;

    // prints PASS or FAIL for one test
    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }

    // code that must compile: returns the assembly
    private static String good(String name, String src) {
        try {
            StrCompiler c = new StrCompiler();
            String asm = c.compile(src);
            check("valid: " + name, true);
            return asm;
        } catch (StrCompileException e) {
            check("valid: " + name + " (" + e.getMessage() + ")", false);
            return "";
        }
    }

    // code that must give an error containing 'part'
    private static void bad(String name, String src, String part) {
        try {
            StrCompiler c = new StrCompiler();
            c.compile(src);
            check("invalid: " + name + " (was accepted!)", false);
        } catch (StrCompileException e) {
            boolean ok = e.getMessage().contains(part);
            check("invalid: " + name, ok);
            if (!ok) {
                System.out.println("      got: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== String module tests ==");

        lexerTests();
        parserTests();
        invalidProgramTests();
        checkerTests();
        codegenTests();
        abstractClassTests();

        System.out.println("\n== RESULT: " + passed + " passed, " + failed + " failed ==");
        System.exit(failed == 0 ? 0 : 1);
    }

    // 1. Lexer: text becomes the right tokens
    private static void lexerTests() throws Exception {
        StrLexer lexer = new StrLexer();
        lexer.read_Source("String name = \"Ayush\";");
        lexer.tokenize();
        ArrayList<Token> t = lexer.getTokens();

        check("lexer: 6 tokens for one declaration", t.size() == 6);
        check("lexer: String is STRING_KW", t.get(0).getType().equals(Token.STRING_KW));
        check("lexer: name is ID", t.get(1).getType().equals(Token.ID));
        check("lexer: = is EQUALS", t.get(2).getType().equals(Token.EQUALS));
        check("lexer: \"Ayush\" is STRING_LIT", t.get(3).getType().equals(Token.STRING_LIT));
        check("lexer: STRING_LIT text has no quotes", t.get(3).getText().equals("Ayush"));
        check("lexer: ; is SEMI", t.get(4).getType().equals(Token.SEMI));
        check("lexer: last token is EOF", t.get(5).getType().equals(Token.EOF));

        lexer.read_Source("String a = \"\";");
        lexer.tokenize();
        check("lexer: empty string is allowed", lexer.getTokens().get(3).getText().equals(""));

        lexer.read_Source("a + b");
        lexer.tokenize();
        check("lexer: + is PLUS", lexer.getTokens().get(1).getType().equals(Token.PLUS));

        lexer.read_Source("string Strings");
        lexer.tokenize();
        check("lexer: 'string' (small s) is an ID, not the keyword",
                lexer.getTokens().get(0).getType().equals(Token.ID));
        check("lexer: 'Strings' is an ID, not the keyword",
                lexer.getTokens().get(1).getType().equals(Token.ID));

        lexer.read_Source("  String\t\n a  ");
        lexer.tokenize();
        check("lexer: spaces, tabs and new lines are skipped", lexer.getTokens().size() == 3);
    }

    // runs only lexer + parser (no checker)
    private static ArrayList<Declaration> parseOnly(String src) throws Exception {
        StrLexer lexer = new StrLexer();
        lexer.read_Source(src);
        lexer.tokenize();

        StrParser parser = new StrParser();
        parser.read_Tokens(lexer.getTokens());
        parser.parse();
        return parser.getDeclarations();
    }

    // 2. Parser: tokens become the right tree
    private static void parserTests() throws Exception {
        ArrayList<Declaration> d = parseOnly("String a = \"x\";");
        check("parser: one declaration", d.size() == 1);
        check("parser: name is a", d.get(0).getName().equals("a"));
        check("parser: value is a StringLiteral", d.get(0).getValue() instanceof StringLiteral);

        d = parseOnly("String a = \"x\"; String b = a;");
        check("parser: two declarations", d.size() == 2);
        check("parser: using a variable gives VarRef", d.get(1).getValue() instanceof VarRef);

        d = parseOnly("String a = \"x\" + \"y\";");
        check("parser: + gives Concat", d.get(0).getValue() instanceof Concat);
        check("parser: show() prints the tree", d.get(0).getValue().show().equals("(\"x\" + \"y\")"));

        d = parseOnly("String a = \"x\" + \"y\" + \"z\";");
        Concat outer = (Concat) d.get(0).getValue();
        check("parser: chain is left-to-right (left side is a Concat)", outer.getLeft() instanceof Concat);
        check("parser: chain right side is a literal", outer.getRight() instanceof StringLiteral);

        d = parseOnly("");
        check("parser: empty program is valid", d.size() == 0);
    }

    // 3. Invalid programs: each one must give the right error message
    private static void invalidProgramTests() {
        // lexer errors
        bad("unterminated string", "String a = \"hi;", "Unterminated string");
        bad("new line inside a string", "String a = \"hi\n\";", "Unterminated string");
        bad("number instead of string", "String a = 5;", "Unknown character '5'");
        bad("single quotes", "String a = 'x';", "Unknown character");

        // parser errors
        bad("missing variable name", "String = \"x\";", "Expected variable name");
        bad("keyword used as name", "String String = \"x\";", "Expected variable name");
        bad("missing =", "String a \"x\";", "Expected '='");
        bad("missing ;", "String a = \"x\"", "Expected ';'");
        bad("missing value", "String a = ;", "Expected a string or variable");
        bad("dangling +", "String a = \"x\" + ;", "Expected a string or variable");
        bad("missing String keyword", "a = \"x\";", "Expected 'String'");

        // error position: opening quote is at index 11
        try {
            StrCompiler c = new StrCompiler();
            c.compile("String a = \"hi;");
            check("error position reported", false);
        } catch (StrCompileException e) {
            check("error position is the opening quote (11)", e.getPosition() == 11);
        }
    }

    // 4. Checker: variables must be declared before use, and only once
    private static void checkerTests() {
        good("use a declared variable", "String a = \"x\"; String b = a;");
        good("use a variable inside +", "String a = \"x\"; String b = \"y\" + a + a;");

        bad("undeclared variable", "String a = b;", "not declared");
        bad("undeclared variable inside +", "String a = \"x\" + b;", "not declared");
        bad("used before it is declared", "String b = a; String a = \"x\";", "not declared");
        bad("self reference", "String a = a;", "not declared");
        bad("declared twice", "String a = \"x\"; String a = \"y\";", "already declared");
    }

    // 5. Codegen: the assembly text has the right lines
    private static void codegenTests() throws Exception {
        String asm = good("simple declaration", "String name = \"Ayush\";");
        check("asm: bits 64", asm.contains("bits 64"));
        check("asm: .data section", asm.contains("section .data"));
        check("asm: .text section", asm.contains("section .text"));
        check("asm: variable has s_ prefix and ends with 0", asm.contains("s_name: db \"Ayush\", 0"));
        check("asm: library routine is str_main", asm.contains("global str_main") && asm.contains("str_main:"));
        check("asm: library mode has no _start", !asm.contains("_start"));
        check("asm: library mode has no syscall", !asm.contains("syscall"));
        check("asm: ends with the GNU-stack note",
                asm.endsWith("section .note.GNU-stack noalloc noexec nowrite progbits\n"));

        asm = good("concatenation", "String name = \"Ayush\"; String greet = \"Hello \" + name;");
        check("asm: concatenation is joined", asm.contains("s_greet: db \"Hello Ayush\", 0"));

        asm = good("chain of variables", "String a = \"x\"; String b = a + a; String c = b + a;");
        check("asm: variables built from variables", asm.contains("s_c: db \"xxx\", 0"));

        asm = good("empty string", "String e = \"\";");
        check("asm: empty string", asm.contains("s_e: db \"\", 0"));

        asm = good("empty program", "");
        check("asm: empty program still has str_main", asm.contains("str_main:"));

        // standalone mode adds _start that prints name=value
        StrCompiler c = new StrCompiler();
        c.setStandalone(true);
        asm = c.compile("String name = \"Ayush\";");
        check("asm: standalone has _start", asm.contains("_start:"));
        check("asm: standalone calls str_main", asm.contains("call str_main"));
        check("asm: standalone prints name=value", asm.contains("d_name: db \"name=Ayush\", 10"));
        check("asm: standalone uses write syscall", asm.contains("mov eax, 1") && asm.contains("syscall"));
        check("asm: standalone exits with syscall 60", asm.contains("mov eax, 60"));
    }

    // 6. Abstract classes: a parent variable can hold a child object
    private static void abstractClassTests() throws Exception {
        TypeCompiler t = new StrCompiler();
        check("abstract: typeName() is String", t.typeName().equals("String"));
        check("abstract: compile() works through TypeCompiler",
                t.compile("String a = \"x\";").contains("s_a: db \"x\", 0"));

        Expr e = new StringLiteral();
        ((StringLiteral) e).read_StringLiteral("hi");
        check("abstract: Expr.show() works through the child class", e.show().equals("\"hi\""));
    }
}
