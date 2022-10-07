package node;

import error.ErrorRet;

public class FuncFParamsNode extends Node{
    public FuncFParamsNode(SyntaxKind input) {
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
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (!(each instanceof TerminalTkNode)) {
                ret.paramDimension.add(tmp.dimension);
            }
        }
        return ret;
    }
}
