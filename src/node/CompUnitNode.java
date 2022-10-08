package node;

import error.ErrorRet;

public class CompUnitNode extends Node{

    public CompUnitNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    public ErrorRet check(){
        symbol.startBlock();
        ErrorRet ret = new ErrorRet();
        try {
            for (Node i : getChildren()) {
                ret.errorList.addAll(i.check().errorList);
            }
        } catch (Exception err) {
            StackTraceElement tmp = err.getStackTrace()[0];
            System.out.println(tmp.getClassName());
            if (tmp.getClassName().equals("java.util.Stack")) {
                throw err;
            }
        }
        symbol.endBlock();
        return ret;
    }
}
