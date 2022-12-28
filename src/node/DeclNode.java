package node;

import error.ErrorRet;

public class DeclNode extends Node{
    public DeclNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        for (Node each:getChildren()) {
            ret.errorList.addAll(each.check().errorList);
        }
        return ret;
    }

}
