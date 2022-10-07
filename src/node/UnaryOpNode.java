package node;

import error.ErrorRet;

public class UnaryOpNode extends Node{
    public UnaryOpNode(SyntaxKind input) {
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
        ret.str = ((TerminalTkNode) getChildren().get(0)).getWord().getText();
        return ret;
    }
}
