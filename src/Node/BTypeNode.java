package Node;

public class BTypeNode extends Node{
    public BTypeNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
