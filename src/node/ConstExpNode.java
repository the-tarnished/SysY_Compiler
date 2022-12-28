package node;

import error.Context;
import error.ErrorRet;
import error.IRRet;

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

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        ctx.isConst = true;
        super.buildIR(ctx, ret);
        ctx.isConst = false;
    }
}
