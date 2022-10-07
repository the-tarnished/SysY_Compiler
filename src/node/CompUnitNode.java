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

    public ErrorRet check() {
        symbol.startBlock();
        ErrorRet ret = new ErrorRet();
        for (Node i : getChildren()) {
            ret.errorList.addAll(i.check().errorList);
        }
        symbol.endBlock();
        return ret;
    }
}
