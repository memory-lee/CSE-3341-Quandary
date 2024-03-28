package ast;

import java.util.HashMap;

public class PrintStmt extends Stmt {

    final Expr expr;

    public PrintStmt(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "print(" + this.expr.toString() + ");";
    }

    @Override
    Object exec(HashMap<String, Long> variables) {
        System.out.println(expr.eval(variables));
        return null;
    }
}