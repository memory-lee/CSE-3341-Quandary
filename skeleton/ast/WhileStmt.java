package ast;

public class WhileStmt extends Stmt {

    private Cond cond;
    private Stmt stmt;

    public WhileStmt(Cond cond, Stmt stmt, Location loc) {
        super(loc);
        this.cond = cond;
        this.stmt = stmt;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt() {
        return stmt;
    }

}
