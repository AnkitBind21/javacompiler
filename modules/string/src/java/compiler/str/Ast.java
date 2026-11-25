package compiler.str;

abstract class Expr {
    abstract String show();
}

class StringLiteral extends Expr {
    private String value;

    public void read_StringLiteral(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    String show() {
        return "\"" + value + "\"";
    }
}

class VarRef extends Expr {
    private String name;

    public void read_VarRef(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    String show() {
        return name;
    }
}

class Concat extends Expr {
    private Expr left;
    private Expr right;

    public void read_Concat(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    public Expr getLeft() {
        return left;
    }
    public Expr getRight() {
        return right; }

    String show() {
        return "(" + left.show() + " + " + right.show() + ")";
    }
}

class Declaration {
    private String name;
    private Expr value;

    public void read_Declaration(String name, Expr value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public Expr getValue() {
        return value;
    }
    public void display() {
        System.out.println("String " + name + " = " + value.show() + ";");
    }
}