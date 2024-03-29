package ast;

public class ExprList extends ASTNode {

    final Expr first;
    final ExprList rest;

    public ExprList(Expr first, ExprList rest, Location loc) {
        super(loc);
        this.first = first;
        this.rest = rest;
    }

    public Expr getFirst() {
        return first;
    }

    public ExprList getRest() {
        return rest;
    }

    @Override
    public String toString() {
        return null;
    }
}
