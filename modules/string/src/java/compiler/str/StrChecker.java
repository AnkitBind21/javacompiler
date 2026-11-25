package compiler.str;
import java.util.ArrayList;

class StrChecker {
    private ArrayList<Declaration> declarations;
    private ArrayList<String> declaredNames;

    public void read_Declarations(ArrayList<Declaration> declarations) {
        this.declarations = declarations;
        this.declaredNames = new ArrayList<String>();
    }
    public void check() throws StrCompileException {
        for (int i = 0; i < declarations.size(); i++) {
            Declaration d = declarations.get(i);

            checkExpr(d.getValue());
            if (declaredNames.contains(d.getName())) {
                throw new StrCompileException("Variable '" + d.getName() + "' is already declared", i);
            }
            declaredNames.add(d.getName());
        }
    }
    private void checkExpr(Expr e) throws StrCompileException {
        if (e instanceof VarRef) {
            VarRef v = (VarRef) e;
            if (!declaredNames.contains(v.getName())) {
                throw new StrCompileException("Variable '" + v.getName() + "' is not declared", declaredNames.size());
            }
        } else if (e instanceof Concat) {
            Concat c = (Concat) e;
            checkExpr(c.getLeft());
            checkExpr(c.getRight());
        }
    }
    public ArrayList<String> getDeclaredNames() {
        return declaredNames;
    }
    public void display() {
        System.out.println("Declared variables: " + declaredNames);
    }
}