package ast;

import java.util.HashMap;

public class UnaryMinusExpr extends Expr {

    public static final int UMINUS = 1;

    final int operator;
    final Expr expr;

    public UnaryMinusExpr(int operator, Expr expr, Location loc) {
        super(loc);
        this.operator = operator;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "(" + simpleString() + ")";
    }

    public String simpleString() {
        return "-" + " " + expr;
    }

    @Override
    Object eval(HashMap<String, Long> variables) {
        if (operator == UMINUS) {
            return -(Long) expr.eval(variables);
        }
        throw new RuntimeException("Unexpected operator in UnaryExpr.eval");
    }
}