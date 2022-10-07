package node;

public class BlockItemNode extends Node{

    public BlockItemNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
