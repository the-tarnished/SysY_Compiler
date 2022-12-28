package node;

import error.Context;
import error.ErrorRet;
import error.IRRet;

public class FuncRParamsNode extends Node{

    public FuncRParamsNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    @Override
    public ErrorRet check() {
        symbol.setInRealParam(true);
        ErrorRet ret = new ErrorRet();
        for (Node each:getChildren()) {
            if (each instanceof BlockNode) {
                symbol.startBlock();
            }
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (!(each instanceof TerminalTkNode)) {
                ret.paramDimension.add(tmp.dimension);
            }
        }
        symbol.setInRealParam(false);
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (each instanceof TerminalTkNode) {
                continue;
            }
            ret.args.add(tmp.ret);
        }
    }
}
