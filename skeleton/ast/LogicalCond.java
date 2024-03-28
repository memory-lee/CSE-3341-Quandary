package ast;

import java.util.HashMap;
import java.util.Optional;

public class LogicalCond extends Cond {

    public static final int AND = 1;
    public static final int OR = 2;
    public static final int NOT = 3;

    private final Optional<Cond> expr1;
    private final Optional<Cond> expr2;
    private final int operator;

    public LogicalCond(Cond expr1, int operator, Cond expr2, Location loc) {
        super(loc);
        this.expr1 = Optional.of(expr1);
        this.expr2 = Optional.of(expr2);
        this.operator = operator;
    }

    public LogicalCond(int operator, Cond cond, Location loc) {
        super(loc);
        this.expr1 = Optional.of(cond);
        this.expr2 = Optional.empty();
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "(" + simpleString() + ")";
    }

    public String simpleString() {
        switch (operator) {
            case AND:
                return expr1.get() + " && " + expr2.get();
            case OR:
                return expr1.get() + " || " + expr2.get();
            case NOT:
                return "!" + expr1.get();
            default:
                throw new RuntimeException("Unexpected operator in LogicalCond.simpleString");
        }
    }

    @Override
    boolean eval(HashMap<String, Long> variables) {
        switch (operator) {
            case AND:
                return expr1.get().eval(variables) && expr2.get().eval(variables);
            case OR:
                return expr1.get().eval(variables) || expr2.get().eval(variables);
            case NOT:
                return !expr1.get().eval(variables);
            default:
                throw new RuntimeException("Unexpected operator in LogicalCond.eval");
        }
    }
}
