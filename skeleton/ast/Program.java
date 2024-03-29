package ast;

import java.io.PrintStream;

public class Program extends ASTNode {

    final FuncDefList funcs;

    public Program(FuncDefList funcs, Location loc) {
        super(loc);
        this.funcs = funcs;
    }

    public FuncDefList getFuncs() {
        return funcs;
    }

    public void println(PrintStream ps) {
        // ps.println(expr);
    }
}