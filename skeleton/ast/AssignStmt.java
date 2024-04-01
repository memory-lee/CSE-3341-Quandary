package ast;

public class AssignStmt extends Stmt {

    final String varName;
    final Expr expr;

    public AssignStmt(String varName, Expr expr, Location loc) {
        super(loc);
        this.varName = varName;
        this.expr = expr;
    }

    public String getVarName() {
        return varName;
    }

    public Expr getExpr() {
        return expr;
    }

}
