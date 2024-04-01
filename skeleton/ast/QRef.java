package ast;

public class QRef extends QVal {

    final QObj referent;

    public QRef(QObj referent) {
        this.referent = referent;
    }

    @Override
    public String toString() {
        if (this.referent == null) {
            return "nil";
        }
        return this.referent.toString();
    }

}
