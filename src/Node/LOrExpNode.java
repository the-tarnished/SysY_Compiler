package Node;

public class LOrExpNode extends Node{
    public LOrExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }
}
