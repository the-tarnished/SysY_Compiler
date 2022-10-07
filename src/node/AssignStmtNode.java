package node;

import error.ErrorRet;

public class AssignStmtNode extends Node{

    public AssignStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        symbol.setLVal(true);
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (each instanceof LValNode) {
                symbol.setLVal(false);
            }
        }
        return ret;
    }
}
