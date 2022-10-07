package node;

import error.ErrorRet;

public class PrimaryExpNode extends Node{
    public PrimaryExpNode(SyntaxKind input) {
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
        for (Node each:getChildren()) {
            if (!(each instanceof TerminalTkNode)) {
                ret = each.check();
            }
        }
        return ret;
    }
}
