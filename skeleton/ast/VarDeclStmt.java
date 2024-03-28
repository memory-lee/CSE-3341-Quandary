package ast;

import java.util.HashMap;

public class VarDeclStmt extends Stmt {

    final String identifier;
    final Expr rValue;

    public VarDeclStmt(String ident, Expr expr, Location loc) {
        super(loc);
        this.identifier = ident;
        this.rValue = expr;
    }

    @Override
    public String toString() {
        return "int " + this.identifier + " = " + this.rValue;
    }

    @Override
    Object exec(HashMap<String, Long> variables) {
        variables.put(this.identifier, (long) this.rValue.eval(variables));
        return null;
    }
}