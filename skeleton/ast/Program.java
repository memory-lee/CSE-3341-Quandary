package ast;

import java.util.HashMap;
import java.util.List;

public class Program extends ASTNode {

    public static final HashMap<String, FuncDef> FunctionMap = new HashMap<String, FuncDef>();

    final FuncDefList funcDefs;

    public Program(List<FuncDef> funcDefs, Location loc) {
        super(loc);
        this.funcDefs = new FuncDefList(funcDefs, loc);

        for (int i = 0; i < funcDefs.size(); i++) {
            Program.FunctionMap.put(funcDefs.get(i).funcName, funcDefs.get(i));
        }
    }

    public Object exec(long argument) {
        return this.funcDefs.exec(argument);
    }
}