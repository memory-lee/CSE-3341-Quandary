package ast;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class QVal {
    public final AtomicBoolean lock = new AtomicBoolean(false);

}
