package ast;

public class ProgramBody extends ASTNode {
    private Expr expr;
    private ReturnStmt returnStmt;

    public ProgramBody(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
        this.returnStmt = null;
    }

    public ProgramBody(ReturnStmt returnStmt, Location loc) {
        super(loc);
        this.expr = null;
        this.returnStmt = returnStmt;
    }

    public Expr getExpr() {
        return expr;
    }

    public ReturnStmt getReturnStmt() {
        return returnStmt;
    }

}