package ast;

public class FuncDefList extends ASTNode {

    final FuncDef first;
    final FuncDefList rest;

    public FuncDefList(FuncDef first, FuncDefList rest, Location loc) {
        super(loc);
        this.first = first;
        this.rest = rest;
    }

    public FuncDef getFirst() {
        return first;
    }

    public FuncDefList getRest() {
        return rest;
    }

    public FuncDef lookupFuncDef(String funcName) {
        if (first.funcName.equals(funcName)) {
            return first;
        } else {
            return rest.lookupFuncDef(funcName);
        }
    }

    @Override
    public String toString() {
        return null;
    }

}
