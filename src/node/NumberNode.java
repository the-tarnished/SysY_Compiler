package node;

import error.ErrorRet;

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
}
