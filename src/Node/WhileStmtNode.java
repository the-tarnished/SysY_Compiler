package Node;

public class WhileStmtNode extends Node{

    public WhileStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
