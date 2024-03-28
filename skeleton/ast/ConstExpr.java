package ast;

import java.util.HashMap;

public class ConstExpr extends Expr {

    final Object value;

    public ConstExpr(long value, Location loc) {
        super(loc);
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    Object eval(HashMap<String, Long> variables) {
        return value;
    }
}