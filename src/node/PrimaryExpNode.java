package node;

import error.Context;
import error.ErrorRet;
import error.IRRet;

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

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        for (Node each:getChildren()) {
            if (!(each instanceof TerminalTkNode)) {
                each.buildIR(ctx,ret);
            }
        }
    }
}
