package node;

import error.ErrorRet;

public class WhileStmtNode extends Node{

    public WhileStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        boolean flag = symbol.isInLoop();
        if (!flag) {
            symbol.setInLoop(true);
        }
        ErrorRet ret = super.check();
        if (!flag) {
            symbol.setInLoop(false);
        }
        return ret;
    }
}
