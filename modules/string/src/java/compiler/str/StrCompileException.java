package compiler.str;

class StrCompileException extends Exception {
    private int position;

    public StrCompileException(String message, int position) {
        super(message);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
    public void display() {
        System.out.println("Compile Error: " + getMessage() + " (at position " + position + ")");
    }
}