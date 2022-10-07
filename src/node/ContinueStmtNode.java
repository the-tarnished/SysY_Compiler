package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;

public class ContinueStmtNode extends Node{
    public ContinueStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = super.check();
        int line;
        line = ((TerminalTkNode)getChildren().get(0)).getWord().getLine();
        if (!symbol.isInLoop()) {
            ret.errorList.add(new Pair<>(ErrorKind.Break_Continue_Out_Loop,line));
        }
        return ret;
    }
}
