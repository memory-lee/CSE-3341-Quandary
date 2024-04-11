package ast;

public class ConcurrentBinaryExpr extends BinaryExpr {

    public ConcurrentBinaryExpr(Expr left, int operator, Expr right, Location loc) {
        super(left, operator, right, loc);
    }
}
