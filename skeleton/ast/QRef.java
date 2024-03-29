package ast;

public class QRef extends QVal {

    final QObj referent;

    public QRef(QObj referent) {
        this.referent = referent;
    }

}
