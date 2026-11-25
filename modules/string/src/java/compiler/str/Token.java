package compiler.str;

class Token {
    public static final String STRING_KW  = "STRING_KW";
    public static final String ID         = "ID";
    public static final String STRING_LIT = "STRING_LIT";
    public static final String EQUALS     = "EQUALS";
    public static final String PLUS       = "PLUS";
    public static final String SEMI       = "SEMI";
    public static final String EOF        = "EOF";

    private String type;
    private String text;

    public void read_Token(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void display() {
        System.out.println("Type: " + type + ", Text: " + text);
    }
}