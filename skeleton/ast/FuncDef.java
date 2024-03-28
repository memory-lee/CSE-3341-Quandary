package ast;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FuncDef extends ASTNode {

    final String funcName;
    final List<String> formalParams;
    final StmtList stmts;

    public FuncDef(String funcName, List<String> formalParams, List<Stmt> statements, Location loc) {
        super(loc);
        this.funcName = funcName;
        this.formalParams = formalParams;
        this.stmts = new StmtList(statements, loc);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.funcName).append(" (");
        String params = this.formalParams.stream()
                .map(param -> "int " + param)
                .collect(Collectors.joining(", "));
        sb.append(params).append(") ").append(this.stmts);
        return sb.toString();
    }

    public Object exec(List<Long> params) {
        if (params.size() < this.formalParams.size()) {
            throw new IllegalArgumentException("Insufficient arguments provided to function.");
        }

        HashMap<String, Long> localVars = new HashMap<>();
        for (int i = 0; i < this.formalParams.size(); i++) {
            localVars.put(this.formalParams.get(i), params.get(i));
        }

        return this.stmts.exec(localVars);
    }
}
