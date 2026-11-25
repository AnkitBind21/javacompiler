package compiler.str;
import java.util.ArrayList;

abstract class TypeParser {
    protected ArrayList<Token> tokens;
    protected int pos;

    public void read_Tokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }
    protected Token current() {
        return tokens.get(pos);
    }
    protected Token expect(String type, String what) throws StrCompileException {
        Token t = current();
        if (!t.getType().equals(type)) {
            throw new StrCompileException("Expected " + what + " but found '" + t.getText() + "'", pos);
        }
        pos++;
        return t;
    }
    public void parse() throws StrCompileException {
        while (!current().getType().equals(Token.EOF)) {
            parseStatement();
        }
    }
    protected abstract void parseStatement() throws StrCompileException;
}