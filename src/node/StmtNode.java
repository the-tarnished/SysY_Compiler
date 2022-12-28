package node;

import error.ErrorRet;

public class StmtNode extends Node{
    public StmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

}
