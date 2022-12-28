package node;

import error.Context;
import error.ErrorRet;
import error.IRRet;

public class NumberNode extends Node{

    public NumberNode(SyntaxKind input) {
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
        ret.value.add(Integer.parseInt(((TerminalTkNode)getChildren().get(0)).getWord().getText()));
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        ret.ret = ((TerminalTkNode)getChildren().get(0)).getWord().getText();
    }
}
