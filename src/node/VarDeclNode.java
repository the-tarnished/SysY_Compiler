package node;

import error.ErrorRet;
import lexer.Token;

public class VarDeclNode extends Node{
    public VarDeclNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

}
