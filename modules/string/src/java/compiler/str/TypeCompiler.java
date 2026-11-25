package compiler.str;

abstract class TypeCompiler {

    public abstract String typeName();
    public abstract String compile(String source) throws Exception;

    public void compileAndShow(String source) {
        try {
            String assembly = compile(source);
            System.out.println("--- " + typeName() + " x86 assembly ---");
            System.out.println(assembly);
        } catch (StrCompileException e) {
            e.display();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}