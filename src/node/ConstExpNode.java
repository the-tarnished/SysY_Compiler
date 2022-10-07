package node;

import error.ErrorRet;

public class ConstExpNode extends Node{
    public ConstExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        symbol.setConst(true);
        for (Node each : getChildren()) {
            ret = each.check();
        }
        symbol.setConst(false);
        return ret;
    }
}
