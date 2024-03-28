package ast;

import java.util.HashMap;

public class BinaryExpr extends Expr {

    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int TIMES = 3;

    final Expr expr1;
    final Expr expr2;
    final int operator;

    public BinaryExpr(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.operator = operator;

    }

    @Override
    public String toString() {
        return "(" + simpleString() + ")";
    }

    public String simpleString() {
        String s = null;
        switch (operator) {
            case PLUS:
                s = "+";
                break;
            case MINUS:
                s = "-";
                break;
            case TIMES:
                s = "*";
                break;
        }
        return expr1 + " " + s + " " + expr2;
    }

    @Override
    Object eval(HashMap<String, Long> variables) {
        switch (operator) {
            case PLUS:
                return (long) expr1.eval(variables) + (long) expr2.eval(variables);
            case MINUS:
                return (long) expr1.eval(variables) - (long) expr2.eval(variables);
            case TIMES:
                return (long) expr1.eval(variables) * (long) expr2.eval(variables);
            default:
                throw new RuntimeException("Unexpected operator in BinaryExpr.eval");
        }
    }
}