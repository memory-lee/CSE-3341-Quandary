package ast;

public class CallStmt extends Stmt {

    private String funcName;
    private ExprList args;

    public CallStmt(String funcName, ExprList args, Location loc) {
        super(loc);
        this.funcName = funcName;
        this.args = args;
    }

    public String getFuncName() {
        return funcName;
    }

    public ExprList getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return null;
    }

}
