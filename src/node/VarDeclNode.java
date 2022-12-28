package node;

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
