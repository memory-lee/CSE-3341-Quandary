package ast;

import java.util.ArrayList;
import java.util.List;

public class FuncDefList extends ASTNode {

    private static final String MAIN = "main";

    final List<FuncDef> funcDefs;

    public FuncDefList(List<FuncDef> funcDefs, Location loc) {
        super(loc);
        this.funcDefs = funcDefs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (FuncDef funcDef : this.funcDefs) {
            sb.append(funcDef).append("\n\r");
        }
        return sb.toString();
    }

    public Object exec(long argument) {
        FuncDef main = getMain(this.funcDefs);
        if (main != null) {
            List<Long> mainArgs = new ArrayList<>();
            mainArgs.add(argument);
            return main.exec(mainArgs);
        } else {
            return null;
        }
    }

    private static FuncDef getMain(List<FuncDef> funcDefs) {
        return funcDefs.stream()
                .filter(f -> MAIN.equals(f.funcName))
                .findFirst()
                .orElse(null);
    }
}
