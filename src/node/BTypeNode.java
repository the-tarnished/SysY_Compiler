package node;

import error.ErrorRet;

public class BTypeNode extends Node{
    public BTypeNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        return new ErrorRet();
    }

}
