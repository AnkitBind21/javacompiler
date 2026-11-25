package compiler.str;
import java.util.ArrayList;

class StrParser extends TypeParser {
    private ArrayList<Declaration> declarations;

    public void read_Tokens(ArrayList<Token> tokens) {
        super.read_Tokens(tokens);
        this.declarations = new ArrayList<Declaration>();
    }
    protected void parseStatement() throws StrCompileException {
        Declaration d = parseDeclaration();
        declarations.add(d);
    }
    private Declaration parseDeclaration() throws StrCompileException {
        expect(Token.STRING_KW, "'String'");
        Token nameToken = expect(Token.ID, "variable name");
        expect(Token.EQUALS, "'='");
        Expr value = parseExpr();
        expect(Token.SEMI, "';'");

        Declaration d = new Declaration();
        d.read_Declaration(nameToken.getText(), value);
        return d;
    }
    private Expr parseExpr() throws StrCompileException {
        Expr left = parsePrimary();

        while (current().getType().equals(Token.PLUS)) {
            pos++;
            Expr right = parsePrimary();

            Concat c = new Concat();
            c.read_Concat(left, right);
            left = c;
        }
        return left;
    }
    private Expr parsePrimary() throws StrCompileException {
        Token t = current();

        if (t.getType().equals(Token.STRING_LIT)) {
            pos++;
            StringLiteral s = new StringLiteral();
            s.read_StringLiteral(t.getText());
            return s;
        } else if (t.getType().equals(Token.ID)) {
            pos++;
            VarRef v = new VarRef();
            v.read_VarRef(t.getText());
            return v;
        } else {
            throw new StrCompileException("Expected a string or variable but found '" + t.getText() + "'", pos);
        }
    }
    public ArrayList<Declaration> getDeclarations() {
        return declarations;
    }
    public void display() {
        for (int i = 0; i < declarations.size(); i++) {
            declarations.get(i).display();
        }
    }
}