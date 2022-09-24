package Node;

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
