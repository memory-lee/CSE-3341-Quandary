package ast;

public class LogicalCond extends Cond {

    public static final int AND = 1;
    public static final int OR = 2;
    public static final int NOT = 3;

    final Cond cond1;
    final int operator;
    final Cond cond2;

    public LogicalCond(Cond cond1, int operator, Cond cond2, Location loc) {
        super(loc);
        this.cond1 = cond1;
        this.operator = operator;
        this.cond2 = cond2;
    }

    public Cond getLeftCond() {
        return cond1;
    }

    public int getOperator() {
        return operator;
    }

    public Cond getRightCond() {
        return cond2;
    }

    @Override
    public String toString() {
        return null;
    }
}
