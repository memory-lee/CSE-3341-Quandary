package ast;

public class QRef extends QVal {

    final QObj referent;

    public QRef(QObj referent) {
        this.referent = referent;
    }

    public QObj getReferent() {
        return this.referent;
    }

    public boolean isNil() {
        return this.referent == null;
    }

    @Override
    public String toString() {
        if (this.referent == null) {
            return "nil";
        }
        return this.referent.toString();
    }

}
