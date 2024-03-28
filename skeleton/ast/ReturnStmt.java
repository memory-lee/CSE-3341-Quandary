package ast;

import java.util.HashMap;

public class ReturnStmt extends Stmt {

    final Expr expr;

    public ReturnStmt(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "return " + this.expr.toString() + ";";
    }

    @Override
    Object exec(HashMap<String, Long> variables) {
        return this.expr.eval(variables);
    }
}