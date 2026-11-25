package compiler.str;
import java.util.Scanner;

public class StrCompiler extends TypeCompiler {
    private StrLexer lexer = new StrLexer();
    private StrParser parser = new StrParser();
    private StrChecker checker = new StrChecker();
    private StrCodegen codegen = new StrCodegen();

    public String typeName() {
        return "String";
    }
    public void setStandalone(boolean standalone) {
        codegen.setStandalone(standalone);
    }
    public String compile(String source) throws StrCompileException {
        lexer.read_Source(source);
        lexer.tokenize();

        parser.read_Tokens(lexer.getTokens());
        parser.parse();

        checker.read_Declarations(parser.getDeclarations());
        checker.check();

        codegen.read_Declarations(parser.getDeclarations());
        codegen.generate();
        return codegen.getAssembly();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter String code (empty line to finish):");
        String source = "";
        String line = sc.nextLine();
        while (!line.equals("")) {
            source = source + line + "\n";
            line = sc.nextLine();
        }

        StrCompiler compiler = new StrCompiler();
        compiler.setStandalone(true);
        compiler.compileAndShow(source);
    }
}