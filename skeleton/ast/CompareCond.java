package ast;

import java.util.HashMap;

public class CompareCond extends Cond {

    public static final int LESS_THAN = 1;
    public static final int LESS_THAN_OR_EQUAL_TO = 2;
    public static final int GREATER_THAN = 3;
    public static final int GREATER_THAN_OR_EQUAL_TO = 4;
    public static final int EQUAL_TO = 5;
    public static final int NOT_EQUAL_TO = 6;

    final Expr expr1;
    final int operator;
    final Expr expr2;

    public CompareCond(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }

    @Override
    public String toString() {
        return "(" + simpleString() + ")";
    }

    public String simpleString() {
        String s = null;
        switch (operator) {
            case LESS_THAN:
                s = "<";
                break;
            case LESS_THAN_OR_EQUAL_TO:
                s = "<=";
                break;
            case GREATER_THAN:
                s = ">";
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                s = ">=";
                break;
            case EQUAL_TO:
                s = "==";
                break;
            case NOT_EQUAL_TO:
                s = "!=";
                break;
        }
        return expr1 + " " + s + " " + expr2;
    }

    @Override
    boolean eval(HashMap<String, Long> variables) {
        long value1 = (long) expr1.eval(variables);
        long value2 = (long) expr2.eval(variables);

        return switch (operator) {
            case LESS_THAN -> value1 < value2;
            case LESS_THAN_OR_EQUAL_TO -> value1 <= value2;
            case GREATER_THAN -> value1 > value2;
            case GREATER_THAN_OR_EQUAL_TO -> value1 >= value2;
            case EQUAL_TO -> value1 == value2;
            case NOT_EQUAL_TO -> value1 != value2;
            default -> throw new RuntimeException("Unexpected operator in BinaryExprCond.eval");
        };
    }
}