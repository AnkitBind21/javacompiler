package compiler.str;
import java.util.ArrayList;

class StrLexer {
    private String source;
    private int pos;
    private ArrayList<Token> tokens;

    public void read_Source(String source) {
        this.source = source;
        this.pos = 0;
        this.tokens = new ArrayList<Token>();
    }

    private void addToken(String type, String text) {
        Token t = new Token();
        t.read_Token(type, text);
        tokens.add(t);
    }

    public void tokenize() throws StrCompileException {
        while (pos < source.length()) {
            char c = source.charAt(pos);

            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                pos++;
            } else if (Character.isLetter(c) || c == '_') {
                int start = pos;
                while (pos < source.length()
                        && (Character.isLetterOrDigit(source.charAt(pos)) || source.charAt(pos) == '_')) {
                    pos++;
                }
                String word = source.substring(start, pos);

                if (word.equals("String")) {
                    addToken(Token.STRING_KW, word);
                } else {
                    addToken(Token.ID, word);
                }
            } else if (c == '"') {
                int start = pos;
                pos++;
                int textStart = pos;

                while (pos < source.length() && source.charAt(pos) != '"' && source.charAt(pos) != '\n') {
                    pos++;
                }

                if (pos >= source.length() || source.charAt(pos) == '\n') {
                    throw new StrCompileException("Unterminated string", start);
                }

                String text = source.substring(textStart, pos);
                addToken(Token.STRING_LIT, text);
                pos++;
            } else if (c == '=') {
                addToken(Token.EQUALS, "=");
                pos++;
            } else if (c == '+') {
                addToken(Token.PLUS, "+");
                pos++;
            } else if (c == ';') {
                addToken(Token.SEMI, ";");
                pos++;
            } else {
                throw new StrCompileException("Unknown character '" + c + "'", pos);
            }
        }

        addToken(Token.EOF, "");
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void display() {
        for (int i = 0; i < tokens.size(); i++) {
            tokens.get(i).display();
        }
    }
}