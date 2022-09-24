package Node;

public class PrimaryExpNode extends Node{
    public PrimaryExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }
}
