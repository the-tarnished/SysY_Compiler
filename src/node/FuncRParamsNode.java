package node;

import error.ErrorRet;

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
}
