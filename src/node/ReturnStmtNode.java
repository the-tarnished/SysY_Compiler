package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;

public class ReturnStmtNode extends Node{
    public ReturnStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        boolean hasExp = false;
        int line = ((TerminalTkNode)getChildren().get(0)).getWord().getLine();
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            hasExp = hasExp || each instanceof ExpNode;
        }
        if (symbol.isVoid() && hasExp) {
            ret.errorList.add(new Pair<>(ErrorKind.Void_Return_Invalid,line));
        }
        ret.hasReturn = true;
        return ret;
    }
}
