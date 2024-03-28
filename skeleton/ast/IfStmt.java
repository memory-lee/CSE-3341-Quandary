package ast;

import java.util.HashMap;

public class IfStmt extends Stmt {

    final Cond cond;
    final Stmt ifStmt;
    final Stmt elseStmt;

    public IfStmt(Cond cond, Stmt ifStmt, Stmt elseStmt, Location loc) {
        super(loc);
        this.cond = cond;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("if (").append(this.cond.toString()).append(") \n\r\t")
                .append(this.ifStmt.toString());

        if (elseStmt != null) {
            sb.append("\n\r\telse\n\r\t").append(this.elseStmt.toString());
        }

        return sb.toString();
    }

    @Override
    Object exec(HashMap<String, Long> variables) {
        if (this.cond.eval(variables)) {
            return this.ifStmt.exec(variables);
        } else if (this.elseStmt != null) {
            return this.elseStmt.exec(variables);
        }
        return null;
    }
}