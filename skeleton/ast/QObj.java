package ast;

public class QObj {

    private QVal left;
    private QVal right;

    public QObj(QVal left, QVal right) {
        this.left = left;
        this.right = right;
    }

    public QVal getLeft() {
        return left;
    }

    public QVal getRight() {
        return right;
    }

    public void setLeft(QVal left) {
        this.left = left;
    }

    public void setRight(QVal right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + this.left.toString() + " . " + this.right.toString() + ")";
    }

}
