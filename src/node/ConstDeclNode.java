package node;

import error.ErrorRet;
import lexer.Token;

public class ConstDeclNode extends Node{
    public ConstDeclNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }
}
