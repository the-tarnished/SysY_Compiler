package Node;

public class ReturnStmtNode extends Node{
    public ReturnStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
