package Node;

public class AssignStmtNode extends Node{

    public AssignStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
