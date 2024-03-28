package ast;

import java.util.HashMap;

public abstract class Stmt extends ASTNode {

    Stmt(Location loc) {
        super(loc);
    }

    abstract Object exec(HashMap<String, Long> variables);
}