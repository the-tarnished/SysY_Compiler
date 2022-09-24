package Node;

public class ContinueStmtNode extends Node{
    public ContinueStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
