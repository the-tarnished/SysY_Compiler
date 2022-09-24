package Node;

public class DeclNode extends Node{
    public DeclNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
