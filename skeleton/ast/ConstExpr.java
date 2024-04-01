package ast;

public class ConstExpr extends Expr {

    final QVal value;

    public ConstExpr(long value, Location loc) {
        super(loc);
        this.value = new QInt(value);
    }

    public QVal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}