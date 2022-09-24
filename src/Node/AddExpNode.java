package Node;

public class AddExpNode extends Node{
    public AddExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }
}
