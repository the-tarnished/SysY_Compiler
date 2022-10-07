package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;

public class ErrorNode extends Node{
    private ErrorKind errorKind;
    private int line;

    public ErrorNode(ErrorKind errorKind,int line) {
        super(null);
        this.errorKind = errorKind;
        this.line = line;
    }

    @Override
    public void print() {
        System.out.println("<Error>");
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        ret.errorList.add(new Pair<>(errorKind,line));
        return ret;
    }
}
