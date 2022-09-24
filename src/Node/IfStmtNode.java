package Node;

public class IfStmtNode extends Node{

    public IfStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
