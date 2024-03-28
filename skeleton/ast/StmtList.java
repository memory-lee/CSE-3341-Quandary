package ast;

import java.util.HashMap;
import java.util.List;

public class StmtList extends Stmt {

    final List<Stmt> stmts;

    public StmtList(List<Stmt> stmts, Location loc) {
        super(loc);
        this.stmts = stmts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\n\r");
        for (Stmt statement : this.stmts) {
            sb.append("\t").append(statement).append("\n\r");
        }
        sb.append("\t}\n\r");
        return sb.toString();
    }

    public Object exec(HashMap<String, Long> variables) {
        for (Stmt statement : this.stmts) {
            Object result = statement.exec(variables);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
