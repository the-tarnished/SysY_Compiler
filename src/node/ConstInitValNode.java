package node;

import error.ErrorRet;

public class ConstInitValNode extends Node{

    public ConstInitValNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

}
