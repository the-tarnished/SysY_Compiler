package Node;

public class BreakStmtNode extends Node{
    public BreakStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
